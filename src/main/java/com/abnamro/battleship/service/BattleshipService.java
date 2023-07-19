package com.abnamro.battleship.service;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Cell;
import com.abnamro.battleship.entity.Game;
import com.abnamro.battleship.entity.Player;
import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.exception.InvalidGameException;
import com.abnamro.battleship.exception.InvalidShipDataException;
import com.abnamro.battleship.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BattleshipService {

    @Autowired
    GameRepository gameRepository;


    public ResponseEntity<String> save(BattleShipRequest battleShipRequest) {

        List<Ship> player1Ships = battleShipRequest.getPlayer1Ships();
        List<Ship> player2Ships = battleShipRequest.getPlayer2Ships();

        validateAndUpdateShipPlacements(player1Ships);
        validateAndUpdateShipPlacements(player2Ships);

        Player player1 = new Player();
        player1.setFleet(player1Ships);
        player1.setName(battleShipRequest.getPlayer1Name());
        player1.setCells(new ArrayList<>());
        player1.getCells().addAll(createEmptyCells());

        Player player2 = new Player();
        player2.setFleet(player2Ships);
        player2.setName(battleShipRequest.getPlayer2Name());
        player2.setCells(new ArrayList<>());
        player2.getCells().addAll(createEmptyCells());

        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setCurrentPlayer(player1);

        game = gameRepository.save(game);

        return ResponseEntity.ok("Game has been set up with gameID: "+game.getId());
    }

    public ResponseEntity<String> attack(Long gameId, String playerName, String position) {
        Game game = gameRepository.findById(gameId).orElseThrow(()
                -> new InvalidGameException("Game not found :"+gameId));

        Player attackingPlayer;
        Player opponentPlayer;

        if (playerName.equals(game.getPlayer1().getName())) {
            attackingPlayer = game.getPlayer1();
            opponentPlayer = game.getPlayer2();
        } else if (playerName.equals(game.getPlayer2().getName())) {
            attackingPlayer = game.getPlayer2();
            opponentPlayer = game.getPlayer1();
        } else {
            throw new InvalidGameException("Invalid player name: "+playerName);
        }

        if(null == game.getCurrentPlayer()){
            throw new InvalidGameException("This game is completed use Setup endpoint to initiate new game");
        }

        if (attackingPlayer != game.getCurrentPlayer()) {
            throw new InvalidGameException("It's not your turn to attack.");
        }

        Cell targetCell = getCellByPosition(opponentPlayer, position);
        if (targetCell == null) {
            throw new InvalidGameException("Invalid attack position.");
        }
        if (targetCell.getStatus() != null) {
            throw new InvalidGameException("Position has already been attacked.");
        }

        targetCell.setStatus("MISS");
        game.setCurrentPlayer(opponentPlayer);
        Ship hitShip = getHitShip(opponentPlayer, position);
        if (hitShip != null) {
            targetCell.setStatus("HIT");
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
                    return ResponseEntity.ok("SUNK");
                }
            }
        }

        if (opponentPlayer.getFleet().isEmpty()) {
            gameRepository.delete(game);
            return ResponseEntity.ok(attackingPlayer.getName() + " wins!");
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

    /**
     * method to validate input data and update ship positions.
     * @param ships - ships to be validated.
     */
    private void validateAndUpdateShipPlacements(List<Ship> ships) {
        if (ships == null || ships.isEmpty()) {
            throw new InvalidShipDataException("No ships found.");
        }

        Set<String> allPositions = new HashSet<>();

        for (Ship ship : ships) {
            if (ship == null) {
                throw new InvalidShipDataException("Invalid ship data.");
            }

            String position = ship.getPosition();
            String orientation = ship.getOrientation();

            if (isInvalidPosition(position)) {
                throw new InvalidShipDataException("Invalid starting position for ship: " + ship.getType());
            }
            if (isInvalidOrientation(orientation.toUpperCase())){
                throw new InvalidShipDataException("Invalid orientation for ship: " + ship.getType());
            }

            int row = position.charAt(0) - 'A';
            int col = Integer.parseInt(position.substring(1)) - 1;

            int shipSize = getShipSize(ship.getType());
            if (shipSize == 0) {
                throw new InvalidShipDataException("Invalid ship type: " + ship.getType());
            }

            int endRow = (orientation.equalsIgnoreCase("horizontal")) ? row : row + shipSize - 1;
            int endCol = (orientation.equalsIgnoreCase("vertical")) ? col : col + shipSize - 1;

            if (endRow >= 10 || endCol >= 10) {
                throw new InvalidShipDataException("Invalid ship placement, " +
                        "ship placement exceeds grid boundaries for: " + ship.getType());
            }

            List<String> positions = new ArrayList<>();
            for (int i = 0; i < shipSize; i++) {
                int currentRow = (orientation.equalsIgnoreCase("horizontal")) ? row : row + i;
                int currentCol = (orientation.equalsIgnoreCase("vertical")) ? col : col + i;
                String shipPosition = String.valueOf((char) ('A' + currentRow)) + (currentCol + 1);

                if (allPositions.contains(shipPosition)) {
                    throw new InvalidShipDataException("Ship placement overlaps with another ship at: "+shipPosition);
                }

                allPositions.add(shipPosition);
                positions.add(shipPosition);
            }
            ship.setPositions(positions);
        }
    }

    private boolean isInvalidOrientation(String orientation) {
        return !("HORIZONTAL".equals(orientation) || "VERTICAL".equals(orientation));
    }


    private boolean isInvalidPosition(String position) {
        if (position.length() < 2 || position.length() > 3) {
            return true;
        }

        int row = position.charAt(0) - 'A';
        int col;
        try {
            col = Integer.parseInt(position.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return true;
        }

        return row < 0 || row >= 10 || col < 0 || col >= 10;
    }

    private int getShipSize(String shipType) {
        switch (shipType) {
            case "Aircraft Carrier":
                return 5;
            case "Battleship":
                return 4;
            case "Cruiser":
            case "Submarine":
                return 3;
            case "Destroyer":
                return 2;
            default:
                return 0;
        }
    }

    private List<Cell> createEmptyCells() {
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                String position = (char) ('A' + i) + String.valueOf(j + 1);
                Cell cell = new Cell();
                cell.setPosition(position);
                cell.setStatus(null);
                cells.add(cell);
            }
        }
        return cells;
    }

}
