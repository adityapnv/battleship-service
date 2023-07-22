package com.abnamro.battleship.util;

public class ErrorMessage {

    public static String NOT_YOUR_TURN = "It's not your turn to attack.";
    public static String INVALID_POSITION = "Invalid attack position ";
    public static String POSITION_ALREADY_ATTACKED = "Position has already been attacked.";
    public static String GAME_COMPLETED = "This game is completed use Setup endpoint to initiate new game";
    public static String GAME_NOT_FOUND = "Game not found ";
    public static String INVALID_PLAYER = "Invalid player name ";
    public static String NO_SHIPS = "No ships found.";
    public static String INVALID_SHIP_DATA = "Invalid ship data.";
    public static String INVALID_STARTING_POSITION = "Invalid starting position for ship ";
    public static String INVALID_SHIP_ORIENTATION = "Invalid orientation for ship ";
    public static String INVALID_SHIP_TYPE = "Invalid ship type ";
    public static String INVALID_SHIP_PLACEMENT = "Invalid ship placement, ship placement exceeds grid boundaries for Ship ";
    public static String SHIP_OVERLAP = "Ship placement overlaps with another ship at: ";
    public static String SHIP_TOUCH_EACH_OTHER = "Ships must not touch each other.";
}
