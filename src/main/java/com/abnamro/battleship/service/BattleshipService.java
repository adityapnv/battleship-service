package com.abnamro.battleship.service;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Game;
import com.abnamro.battleship.entity.Player;
import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.repository.GameRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BattleshipService {

    GameRepository gameRepository;


    public ResponseEntity<String> save(BattleShipRequest battleShipRequest) {

        List<Ship> player1Ships = battleShipRequest.getPlayer1Ships();
        List<Ship> player2Ships = battleShipRequest.getPlayer2Ships();

        String errMsgShip1Placements = validateAndUpdateShipPlacements(player1Ships);
        if (errMsgShip1Placements != null) {
            return ResponseEntity.badRequest().body(errMsgShip1Placements);
        }

        String errMsgShip2Placements = validateAndUpdateShipPlacements(player2Ships);
        if (errMsgShip2Placements != null) {
            return ResponseEntity.badRequest().body(errMsgShip2Placements);
        }

        Player player1 = new Player();
        player1.setFleet(player1Ships);
        player1.setName(battleShipRequest.getPlayer1Name());

        Player player2 = new Player();
        player2.setFleet(player2Ships);
        player2.setName(battleShipRequest.getPlayer2Name());

        Game game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setCurrentPlayer(player1);

        gameRepository.save(game);

        return ResponseEntity.ok("Game has been set up.");
    }

    /**
     * method to validate input data and update ship positions.
     * @param ships - ships to be validated.
     * @return null in case of no validation failure.
     */
    private String validateAndUpdateShipPlacements(List<Ship> ships) {
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
                        (char) ('A' + endRow) + String.valueOf(endCol + 1);
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

        return null;
    }


    private boolean isValidPosition(int row, int col) {
        return row < 0 || row >= 10 || col < 0 || col >= 10;
    }


}
