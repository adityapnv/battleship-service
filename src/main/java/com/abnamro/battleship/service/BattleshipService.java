package com.abnamro.battleship.service;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Cell;
import com.abnamro.battleship.entity.Game;
import com.abnamro.battleship.entity.Player;
import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.exception.InvalidGameException;
import com.abnamro.battleship.repository.GameRepository;
import com.abnamro.battleship.util.BattleshipUtil;
import com.abnamro.battleship.util.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BattleshipService {

    @Autowired
    GameRepository gameRepository;

    /**
     * Service method for setup. Create initial setup required.
     * @param battleShipRequest - Request containing player and player ship details.
     * @return gameID.
     */
    public ResponseEntity<String> save(BattleShipRequest battleShipRequest) {

        List<Ship> player1Ships = battleShipRequest.getPlayer1Ships();
        List<Ship> player2Ships = battleShipRequest.getPlayer2Ships();

        Player player1 = new Player();
        player1.setName(battleShipRequest.getPlayer1Name());
        player1.setFleet(BattleshipUtil.validateAndUpdateShipPlacements(player1Ships, player1.getName()));
        player1.setCells(new ArrayList<>());
        player1.getCells().addAll(BattleshipUtil.createEmptyCells());

        Player player2 = new Player();
        player2.setName(battleShipRequest.getPlayer2Name());
        player2.setFleet(BattleshipUtil.validateAndUpdateShipPlacements(player2Ships, player2.getName()));
        player2.setCells(new ArrayList<>());
        player2.getCells().addAll(BattleshipUtil.createEmptyCells());

        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setCurrentPlayer(player1);

        game = gameRepository.save(game);

        return ResponseEntity.ok("Game has been set up with gameID: "+game.getId());
    }

    /**
     * Method to support play and attack opponent.
     * @param gameId Game ID
     * @param playerName - Name of the attacking player
     * @param position - where to attack
     * @return result.
     */
    public ResponseEntity<String> attack(Long gameId, String playerName, String position) {
        if (BattleshipUtil.isInvalidPosition(position)) {
            throw new InvalidGameException(ErrorMessage.INVALID_POSITION + position);
        }
        Game game = gameRepository.findById(gameId).orElseThrow(()
                -> new InvalidGameException(ErrorMessage.GAME_NOT_FOUND + gameId));

        Player attackingPlayer;
        Player opponentPlayer;

        if (playerName.equals(game.getPlayer1().getName())) {
            attackingPlayer = game.getPlayer1();
            opponentPlayer = game.getPlayer2();
        } else if (playerName.equals(game.getPlayer2().getName())) {
            attackingPlayer = game.getPlayer2();
            opponentPlayer = game.getPlayer1();
        } else {
            throw new InvalidGameException(ErrorMessage.INVALID_PLAYER + playerName);
        }

        if(null == game.getCurrentPlayer()){
            throw new InvalidGameException(ErrorMessage.GAME_COMPLETED);
        }

        if (attackingPlayer != game.getCurrentPlayer()) {
            throw new InvalidGameException(ErrorMessage.NOT_YOUR_TURN);
        }

        Cell targetCell = getCellByPosition(opponentPlayer, position);
        if (targetCell == null) {
            throw new InvalidGameException(ErrorMessage.INVALID_POSITION);
        }
        if (targetCell.getStatus() != null) {
            throw new InvalidGameException(ErrorMessage.POSITION_ALREADY_ATTACKED);
        }

        targetCell.setStatus(BattleshipUtil.MISS);
        game.setCurrentPlayer(opponentPlayer);
        Ship hitShip = getHitShip(opponentPlayer, position);
        if (hitShip != null) {
            targetCell.setStatus(BattleshipUtil.HIT);
            game.setCurrentPlayer(attackingPlayer);
            hitShip.getHitPositions().add(position);

            if (isShipSunk(hitShip)) {
                opponentPlayer.getFleet().remove(hitShip);

                if (opponentPlayer.getFleet().isEmpty()) {
                    game.setCurrentPlayer(null);
                    gameRepository.save(game);
                    return ResponseEntity.ok(attackingPlayer.getName() + " wins! All opponent's ships sunk.");
                } else {
                    gameRepository.save(game);
                    return ResponseEntity.ok(BattleshipUtil.SUNK);
                }
            }
        }

        gameRepository.save(game);

        return ResponseEntity.ok(targetCell.getStatus());
    }

    private Cell getCellByPosition(Player player, String position) {
        return player.getCells().stream()
                .filter(cell -> cell.getPosition().equals(position))
                .findFirst()
                .orElse(null);
    }

    private Ship getHitShip(Player player, String position) {
        return player.getFleet().stream()
                .filter(ship -> ship.getPositions().contains(position))
                .findFirst()
                .orElse(null);
    }

    private boolean isShipSunk(Ship ship) {
        return ship.getPositions().stream().allMatch(ship.getHitPositions()::contains);
    }
}
