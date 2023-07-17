package com.abnamro.battleship.service;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Cell;
import com.abnamro.battleship.entity.Game;
import com.abnamro.battleship.entity.Player;
import com.abnamro.battleship.entity.Ship;
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

        if(null == game.getCurrentPlayer()){
            return ResponseEntity.badRequest().body("This game is completed use Setup endpoint to initiate new game");
        }

        if (attackingPlayer != game.getCurrentPlayer()) {
            return ResponseEntity.badRequest().body("It's not your turn to attack.");
        }

        if (isNotValidPosition(position)) {
            return ResponseEntity.badRequest().body("Invalid attack position.");
        }

        Cell targetCell = getCellByPosition(opponentPlayer, position);
        if (targetCell == null) {
            return ResponseEntity.badRequest().body("Invalid attack position.");
        }

        if (targetCell.getStatus() != null) {
            return ResponseEntity.badRequest().body("Position has already been attacked.");
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
     * @return null in case of no validation failure.
     */
    private String validateAndUpdateShipPlacements(List<Ship> ships) {
        if (ships == null || ships.isEmpty()) {
            return "No ships found.";
        }

        Set<String> allPositions = new HashSet<>();

        for (Ship ship : ships) {
            if (ship == null || ship.getType() == null || ship.getPosition() == null || ship.getOrientation() == null) {
                return "Invalid ship data.";
            }

            String position = ship.getPosition();
            String orientation = ship.getOrientation();

            if (isNotValidPosition(position)) {
                return "Invalid starting position for ship: " + ship.getType();
            }

            int row = position.charAt(0) - 'A';
            int col = Integer.parseInt(position.substring(1)) - 1;

            int shipSize = getShipSize(ship.getType());
            if (shipSize == 0) {
                return "Invalid ship name: " + ship.getType();
            }

            int endRow = (orientation.equalsIgnoreCase("horizontal")) ? row : row + shipSize - 1;
            int endCol = (orientation.equalsIgnoreCase("vertical")) ? col : col + shipSize - 1;

            if (endRow >= 10 || endCol >= 10) {
                return "Invalid ship placement, ship placement exceeds grid boundaries for: " + ship.getType();
            }

            List<String> positions = new ArrayList<>();
            for (int i = 0; i < shipSize; i++) {
                int currentRow = (orientation.equalsIgnoreCase("horizontal")) ? row : row + i;
                int currentCol = (orientation.equalsIgnoreCase("vertical")) ? col : col + i;
                String shipPosition = String.valueOf((char) ('A' + currentRow)) + (currentCol + 1);

                if (allPositions.contains(shipPosition)) {
                    return "Ship placement overlaps with another ship at: "+shipPosition;
                }

                allPositions.add(shipPosition);
                positions.add(shipPosition);
            }

            ship.setPositions(positions);
        }

        return null;
    }


    private boolean isNotValidPosition(String position) {
        if (position.length() < 2 || position.length() > 3) {
            return false;
        }

        int row = position.charAt(0) - 'A';
        int col;
        try {
            col = Integer.parseInt(position.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return false;
        }

        return row < 0 || row >= 10 || col < 0 || col >= 10;
    }

    /*private void populateShipPositions(List<Ship> ships, List<Cell> cells) {
        for (Ship ship : ships) {
            int shipSize = getShipSize(ship.getType());
            List<String> positions = ship.getPositions();

            // Set ship positions in the cells
            for (String position : positions) {
                Cell cell = cells.stream().filter(c -> c.getPosition().equals(position)).findFirst().orElse(null);
                if (cell == null) {
                    cell.setStatus("SHIP");
                } else {
                    System.out.println("------------Something wrong------------------");
                }
            }
        }
    }*/

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
