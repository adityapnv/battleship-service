package com.abnamro.battleship.util;

import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.exception.InvalidShipDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleshipUtilTest {

    List<Ship> playerShips;

    @BeforeEach
    void init() {
        playerShips = new ArrayList<>();
        Ship ship1 = new Ship();
        ship1.setType("Aircraft Carrier");
        ship1.setPosition("A1");
        ship1.setOrientation("horizontal");
        Ship ship2 = new Ship();
        ship2.setType("Battleship");
        ship2.setPosition("C1");
        ship2.setOrientation("vertical");
        Ship ship3 = new Ship();
        ship3.setType("Cruiser");
        ship3.setPosition("D3");
        ship3.setOrientation("horizontal");
        Ship ship4 = new Ship();
        ship4.setType("Submarine");
        ship4.setPosition("F3");
        ship4.setOrientation("vertical");

        playerShips.add(ship1);
        playerShips.add(ship2);
        playerShips.add(ship3);
        playerShips.add(ship4);
    }

    @Test
    public void testValidateAndUpdateShipPlacements(){
        List<String> ship1Positions = Arrays.asList("A1", "A2", "A3", "A4", "A5");
        List<String> ship2Positions = Arrays.asList("C1", "D1", "E1", "F1");
        List<String> ship3Positions = Arrays.asList("D3", "D4", "D5");
        List<String> ship4Positions = Arrays.asList("F3", "G3", "H3");
        List<Ship> updatedShips = BattleshipUtil.validateAndUpdateShipPlacements(playerShips);
        Assertions.assertNotNull(updatedShips);
        Assertions.assertEquals(4, updatedShips.size());
        Assertions.assertEquals(ship1Positions, updatedShips.get(0).getPositions());
        Assertions.assertEquals(ship2Positions, updatedShips.get(1).getPositions());
        Assertions.assertEquals(ship3Positions, updatedShips.get(2).getPositions());
        Assertions.assertEquals(ship4Positions, updatedShips.get(3).getPositions());
    }

    @Test
    public void testOverLapShipPlacement(){
        String position = "A1";
        playerShips.get(1).setPosition(position);
        try {
            BattleshipUtil.validateAndUpdateShipPlacements(playerShips);
        } catch (InvalidShipDataException ex){
            Assertions.assertEquals(ErrorMessage.SHIP_OVERLAP + position, ex.getMessage());
        }
    }
    @Test
    public void testShipTouchEachOtherPlacement(){
        String position = "B1";
        playerShips.get(1).setPosition(position);
        try {
            BattleshipUtil.validateAndUpdateShipPlacements(playerShips);
        } catch (InvalidShipDataException ex){
            Assertions.assertEquals(ErrorMessage.SHIP_TOUCH_EACH_OTHER, ex.getMessage());
        }
    }

    @Test
    public void testInvalidShipType(){
        String invalidShipType = "aaaaa";
        playerShips.get(1).setType(invalidShipType);
        try {
            BattleshipUtil.validateAndUpdateShipPlacements(playerShips);
        } catch (InvalidShipDataException ex){
            Assertions.assertEquals(ErrorMessage.INVALID_SHIP_TYPE + invalidShipType, ex.getMessage());
        }
    }

    @Test
    public void testInvalidShipStartingPosition(){
        String position = "K1";
        playerShips.get(1).setPosition(position);
        try {
            BattleshipUtil.validateAndUpdateShipPlacements(playerShips);
        } catch (InvalidShipDataException ex){
            Assertions.assertEquals(ErrorMessage.INVALID_STARTING_POSITION + playerShips.get(1).getType(), ex.getMessage());
        }
    }

    @Test
    public void testInvalidShipPlacement(){
        String position = "A10";
        playerShips.get(0).setPosition(position);
        try {
            BattleshipUtil.validateAndUpdateShipPlacements(playerShips);
        } catch (InvalidShipDataException ex){
            Assertions.assertEquals(ErrorMessage.INVALID_SHIP_PLACEMENT + playerShips.get(0).getType(), ex.getMessage());
        }
    }

    @Test
    public void testInvalidShipOrientation(){
        playerShips.get(3).setOrientation("Invalid");
        try {
            BattleshipUtil.validateAndUpdateShipPlacements(playerShips);
        } catch (InvalidShipDataException ex){
            Assertions.assertEquals(ErrorMessage.INVALID_SHIP_ORIENTATION + playerShips.get(3).getType(), ex.getMessage());
        }
    }
}
