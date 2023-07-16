package com.abnamro.battleship.service;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Game;
import com.abnamro.battleship.entity.Player;
import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BattleshipService {

    @Autowired
    GameRepository gameRepository;


    public ResponseEntity<String> save(BattleShipRequest battleShipRequest) {

        Player player1 = new Player();
        Player player2 = new Player();
        List<Ship> player1Ships = battleShipRequest.getPlayer1Ships();
        List<Ship> player2Ships = battleShipRequest.getPlayer2Ships();

        String errMsgShip1Placements = validateAndUpdateShipPlacements(player1Ships, player1);
        if (errMsgShip1Placements != null) {
            return ResponseEntity.badRequest().body(errMsgShip1Placements);
        }

        String errMsgShip2Placements = validateAndUpdateShipPlacements(player2Ships, player2);
        if (errMsgShip2Placements != null) {
            return ResponseEntity.badRequest().body(errMsgShip2Placements);
        }

        player1.setFleet(player1Ships);
        player1.setName(battleShipRequest.getPlayer1Name());

        player2.setFleet(player2Ships);
        player2.setName(battleShipRequest.getPlayer2Name());

        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setCurrentPlayer(player1);

        gameRepository.save(game);

        return ResponseEntity.ok("Game has been set up.");
    }

    public ResponseEntity<String> attack(Long gameId, String playerName, String position) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.badRequest().body("Game not found.");
        }

        Player attackingPlayer;
        Player opponentPlayer;

        if (playerName.equals(game.getPlayer1().getName())) {
            attackingPlayer = game.getPlayer1();
            opponentPlayer = game.getPlayer2();
        } else if (playerName.equals(game.getPlayer2().getName())) {
            attackingPlayer = game.getPlayer2();
            opponentPlayer = game.getPlayer1();
        } else {
            return ResponseEntity.badRequest().body("Invalid player name.");
        }

        if (attackingPlayer != game.getCurrentPlayer()) {
            return ResponseEntity.badRequest().body("It's not your turn to attack.");
        }

        int row = position.charAt(0) - 'A';
        int col = Integer.parseInt(position.substring(1)) - 1;

        if (!isValidPosition(row, col)) {
            return ResponseEntity.badRequest().body("Invalid attack position.");
        }

        if (attackingPlayer.getGuesses().contains(position)) {
            return ResponseEntity.badRequest().body("Position has already been attacked.");
        }

        String[][] opponentGrid = opponentPlayer.getGrid();
        attackingPlayer.getGuesses().add(position);

        if (opponentGrid[row][col] != null) {
            opponentGrid[row][col] = "HIT";

            Ship hitShip = opponentPlayer.getFleet().stream()
                    .filter(ship -> ship.getPositions().contains(position))
                    .findFirst().orElse(null);

            if (hitShip != null) {
                hitShip.getPositions().remove(position);

                // Check if the ship is sunk
                if (hitShip.getPositions().isEmpty()) {

                    if (opponentPlayer.getFleet().stream().allMatch(ship -> ship.getPositions().isEmpty())) {
                        game.setCurrentPlayer(null);
                        gameRepository.save(game);
                        // All opponent's ships are sunk, attacking player as the winner.
                        return ResponseEntity.ok(attackingPlayer.getName() + " wins!");
                    }
                    game.setCurrentPlayer(attackingPlayer);
                    gameRepository.save(game); // Save the updated game to the database
                    return ResponseEntity.ok("Sunk");
                }
            }

            game.setCurrentPlayer(attackingPlayer);
            gameRepository.save(game);

            return ResponseEntity.ok("Hit");
        } else {
            opponentGrid[row][col] = "MISS";

            game.setCurrentPlayer(opponentPlayer);
            gameRepository.save(game);

            return ResponseEntity.ok("Miss");
        }
    }

    /**
     * method to validate input data and update ship positions.
     * @param ships - ships to be validated.
     * @param player - player to add grid.
     * @return null in case of no validation failure.
     */
    private String validateAndUpdateShipPlacements(List<Ship> ships, Player player) {
        String[][] grid = new String[10][10];

        for (Ship ship : ships) {
            String position = ship.getPosition();
            String orientation = ship.getOrientation();
            int row = position.charAt(0) - 'A';
            int col = Integer.parseInt(position.substring(1)) - 1;

            if (isValidPosition(row, col)) {
                return "Invalid ship position: " + position;
            }

            int size = ship.getSize();
            int endRow = (orientation.equalsIgnoreCase("horizontal")) ? row : row + size - 1;
            int endCol = (orientation.equalsIgnoreCase("vertical")) ? col : col + size - 1;

            if (isValidPosition(endRow, endCol)) {
                return "Ship placement exceeds grid boundaries: " + position + " - " +
                        (char) ('A' + endRow) + (endCol + 1);
            }

            for (int i = row; i <= endRow; i++) {
                for (int j = col; j <= endCol; j++) {
                    if (grid[i][j] != null) {
                        return "Ship placement overlaps with another ship: " + position;
                    }
                }
            }

            for (int i = row; i <= endRow; i++) {
                for (int j = col; j <= endCol; j++) {
                    grid[i][j] = "SHIP";
                }
            }

            for (int i = 0; i < size; i++) {
                int currentRow = (orientation.equalsIgnoreCase("horizontal")) ? row : row + i;
                int currentCol = (orientation.equalsIgnoreCase("vertical")) ? col : col + i;
                ship.getPositions().add((char) ('A' + currentRow) + String.valueOf(currentCol + 1));
            }
        }
        player.setGrid(grid);
        return null;
    }


    private boolean isValidPosition(int row, int col) {
        return row < 0 || row >= 10 || col < 0 || col >= 10;
    }

}
