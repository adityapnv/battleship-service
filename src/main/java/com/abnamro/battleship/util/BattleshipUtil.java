package com.abnamro.battleship.util;

import com.abnamro.battleship.entity.Cell;
import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.exception.InvalidShipDataException;

import java.util.*;

public class BattleshipUtil {

    public static String HIT = "Hit";
    public static String MISS = "Miss";
    public static String SUNK = "Sunk";
    public static String VERTICAL = "vertical";
    public static String HORIZONTAL = "Horizontal";

    /**
     * method to validate input data and update ship positions.
     * @param ships - ships to be validated.
     * @return updated ships.
     */
    public static List<Ship> validateAndUpdateShipPlacements(List<Ship> ships) {
        if (ships == null || ships.isEmpty()) {
            throw new InvalidShipDataException(ErrorMessage.NO_SHIPS);
        }

        Set<String> allPositions = new HashSet<>();
        Set<String> adjacentPositions = new HashSet<>();

        for (Ship ship : ships) {
            if (ship == null) {
                throw new InvalidShipDataException(ErrorMessage.INVALID_SHIP_DATA);
            }

            String position = ship.getPosition();
            String orientation = ship.getOrientation();

            if (BattleshipUtil.isInvalidPosition(position)) {
                throw new InvalidShipDataException(ErrorMessage.INVALID_STARTING_POSITION + ship.getType());
            }
            if (BattleshipUtil.isInvalidOrientation(orientation.toUpperCase())){
                throw new InvalidShipDataException(ErrorMessage.INVALID_SHIP_ORIENTATION + ship.getType());
            }

            int row = position.charAt(0) - 'A';
            int col = Integer.parseInt(position.substring(1)) - 1;

            int shipSize = BattleshipUtil.getShipSize(ship.getType());
            if (shipSize == 0) {
                throw new InvalidShipDataException(ErrorMessage.INVALID_SHIP_TYPE + ship.getType());
            }

            int endRow = (orientation.equalsIgnoreCase(HORIZONTAL)) ? row : row + shipSize - 1;
            int endCol = (orientation.equalsIgnoreCase(VERTICAL)) ? col : col + shipSize - 1;

            if (endRow >= 10 || endCol >= 10) {
                throw new InvalidShipDataException(ErrorMessage.INVALID_SHIP_PLACEMENT + ship.getType());
            }

            List<String> positions = new ArrayList<>();
            for (int i = 0; i < shipSize; i++) {
                int currentRow = (orientation.equalsIgnoreCase("horizontal")) ? row : row + i;
                int currentCol = (orientation.equalsIgnoreCase("vertical")) ? col : col + i;
                String shipPosition = String.valueOf((char) ('A' + currentRow)) + (currentCol + 1);

                if (allPositions.contains(shipPosition)) {
                    throw new InvalidShipDataException(ErrorMessage.SHIP_OVERLAP +shipPosition);
                }

                allPositions.add(shipPosition);
                positions.add(shipPosition);

            }

            for (int row1 = row - 1; row1 <= endRow + 1; row1++) {
                for (int col1 = col - 1; col1 <= endCol + 1; col1++) {
                    if (!isInvalidPosition(row1, col1)) {
                        String adjacentPosition = String.valueOf((char) ('A' + row1)) + (col1 + 1);
                        if(!positions.contains(adjacentPosition)){
                            adjacentPositions.add(adjacentPosition);
                        }
                    }
                }
            }

            if (!Collections.disjoint(allPositions, adjacentPositions)) {
                throw new InvalidShipDataException(ErrorMessage.SHIP_TOUCH_EACH_OTHER);
            }

            ship.setPositions(positions);
        }

        return ships;
    }

    /**
     * Method to validate ship orientation
     * @param orientation orientation.
     * @return true only if it is either vertical or horizontal
     */
    public static boolean isInvalidOrientation(String orientation) {
        return !(HORIZONTAL.equalsIgnoreCase(orientation) || VERTICAL.equalsIgnoreCase(orientation));
    }


    /**
     * Method to validate position received in the request.
     * @param position ship position or attack position
     * @return true if value is [A to J][1 to 10]
     */
    public static boolean isInvalidPosition(String position) {
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

        return isInvalidPosition(row, col);
    }

    private static boolean isInvalidPosition(int row, int col) {
        return row < 0 || row >= 10 || col < 0 || col >= 10;
    }

    /**
     * Method to get ship size based on the type
     * @param shipType ship type
     * @return size.
     */
    public static int getShipSize(String shipType) {
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

    /**
     * Method to create player cells.
     * @return player board
     */
    public static List<Cell> createEmptyCells() {
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
