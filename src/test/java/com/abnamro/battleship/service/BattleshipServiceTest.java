package com.abnamro.battleship.service;

import com.abnamro.battleship.entity.Game;
import com.abnamro.battleship.entity.Player;
import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.exception.InvalidGameException;
import com.abnamro.battleship.repository.GameRepository;
import com.abnamro.battleship.util.BattleshipUtil;
import com.abnamro.battleship.util.ErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class BattleshipServiceTest {

    @InjectMocks
    BattleshipService battleshipService;

    @Mock
    GameRepository gameRepository;

    Game game;

    @BeforeEach
    void createGame(){
        List<Ship> player1Ships = new ArrayList<>();
        List<Ship> player2Ships = new ArrayList<>();
        Ship ship1 = new Ship();
        ship1.setType("Aircraft Carrier");
        ship1.setPosition("A1");
        ship1.setOrientation("horizontal");
        Ship ship2 = new Ship();
        ship2.setType("Battleship");
        ship2.setPosition("C1");
        ship2.setOrientation("vertical");
        Ship ship3 = new Ship();
        ship3.setType("Destroyer");
        ship3.setPosition("A1");
        ship3.setOrientation("horizontal");
        Ship ship4 = new Ship();
        ship4.setType("Submarine");
        ship4.setPosition("C2");
        ship4.setOrientation("vertical");

        player1Ships.add(ship1);
        player1Ships.add(ship2);
        player2Ships.add(ship3);
        player2Ships.add(ship4);

        Player player1 = new Player();
        player1.setName("Player1");
        player1.setFleet(BattleshipUtil.validateAndUpdateShipPlacements(player1Ships, player1.getName()));
        player1.setCells(new ArrayList<>());
        player1.getCells().addAll(BattleshipUtil.createEmptyCells());

        Player player2 = new Player();
        player2.setName("Player2");
        player2.setFleet(BattleshipUtil.validateAndUpdateShipPlacements(player2Ships, player2.getName()));
        player2.setCells(new ArrayList<>());
        player2.getCells().addAll(BattleshipUtil.createEmptyCells());

        game = new Game();
        game.setPlayer1(player1);
        game.setPlayer2(player2);
        game.setCurrentPlayer(player1);
    }

    @Test
    public void testValidAttackHitCell(){
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        Mockito.when(gameRepository.save(any(Game.class))).thenReturn(new Game());
        ResponseEntity<String> response = battleshipService.attack(1L, "Player1", "A1");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(BattleshipUtil.HIT, response.getBody());
    }

    @Test
    public void testValidAttackMissCell(){
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        Mockito.when(gameRepository.save(any(Game.class))).thenReturn(new Game());
        ResponseEntity<String> response = battleshipService.attack(1L, "Player1", "B10");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(BattleshipUtil.MISS, response.getBody());
    }

    @Test
    public void testValidAttackCellSunkShip(){
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        Mockito.when(gameRepository.save(any(Game.class))).thenReturn(new Game());
        battleshipService.attack(1L, "Player1", "A1");
        ResponseEntity<String> response = battleshipService.attack(1L, "Player1", "A2");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(BattleshipUtil.SUNK, response.getBody());
    }

    @Test
    public void testValidAttackCellPlayer1Winner(){
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        Mockito.when(gameRepository.save(any(Game.class))).thenReturn(new Game());
        battleshipService.attack(1L, "Player1", "A1");
        battleshipService.attack(1L, "Player1", "A2");
        battleshipService.attack(1L, "Player1", "C2");
        battleshipService.attack(1L, "Player1", "D2");
        ResponseEntity<String> response = battleshipService.attack(1L, "Player1", "E2");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Player1 wins! All opponent's ships sunk.", response.getBody());
    }

    @Test
    public void testInvalidAttackCellNotYourTurn(){
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        Mockito.when(gameRepository.save(any(Game.class))).thenReturn(new Game());
        battleshipService.attack(1L, "Player1", "A3");
        try{
            battleshipService.attack(1L, "Player1", "A2");
        } catch (InvalidGameException ex){
            Assertions.assertEquals(ErrorMessage.NOT_YOUR_TURN,ex.getMessage());
        }
    }

    @Test
    public void testInvalidAttackCellGameOver(){
        game.setCurrentPlayer(null);
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        try{
            battleshipService.attack(1L, "Player1", "A1");
        } catch (InvalidGameException ex){
            Assertions.assertEquals(ErrorMessage.GAME_COMPLETED,ex.getMessage());
        }
    }

    @Test
    public void testInvalidAttackCellPositionAlreadyAttacked(){
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        Mockito.when(gameRepository.save(any(Game.class))).thenReturn(new Game());
        battleshipService.attack(1L, "Player1", "A1");
        try{
            battleshipService.attack(1L, "Player1", "A1");
        } catch (InvalidGameException ex){
            Assertions.assertEquals(ErrorMessage.POSITION_ALREADY_ATTACKED,ex.getMessage());
        }
    }

    @Test
    public void testInvalidPlayerAttackCell(){
        Mockito.when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
        String invalidPlayer = "player3";
        try{
            battleshipService.attack(1L, invalidPlayer, "A1");
        } catch (InvalidGameException ex){
            Assertions.assertEquals(ErrorMessage.INVALID_PLAYER + invalidPlayer ,ex.getMessage());
        }
    }

    @Test
    public void testInvalidAttackCellGameNotFound(){
        try{
            battleshipService.attack(1L, "Player1", "A1");
        } catch (InvalidGameException ex){
            Assertions.assertEquals(ErrorMessage.GAME_NOT_FOUND + "1",ex.getMessage());
        }
    }

}
