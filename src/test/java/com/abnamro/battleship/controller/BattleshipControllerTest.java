package com.abnamro.battleship.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Ship;
import com.abnamro.battleship.service.BattleshipService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BattleshipController.class)
public class BattleshipControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    BattleshipService battleshipService;

    BattleShipRequest battleShipRequest;
    ObjectMapper mapper;

    @BeforeEach
    void init() {
        battleShipRequest = new BattleShipRequest();
        List<Ship> player1Ships = new ArrayList<>();
        List<Ship> player2Ships = new ArrayList<>();
        Ship ship1 = new Ship();
        ship1.setType("Aircraft Carrier");
        ship1.setPosition("A1");
        ship1.setOrientation("horizontal");
        Ship ship2 = new Ship();
        ship2.setType("Battleship");
        ship2.setPosition("B1");
        ship2.setOrientation("vertical");
        Ship ship3 = new Ship();
        ship3.setType("Aircraft Carrier");
        ship3.setPosition("A1");
        ship3.setOrientation("horizontal");
        Ship ship4 = new Ship();
        ship4.setType("Battleship");
        ship4.setPosition("B1");
        ship4.setOrientation("vertical");

        player1Ships.add(ship1);
        player1Ships.add(ship2);
        player2Ships.add(ship3);
        player2Ships.add(ship4);

        battleShipRequest.setPlayer1Name("Player1");
        battleShipRequest.setPlayer1Ships(player1Ships);
        battleShipRequest.setPlayer2Name("Player2");
        battleShipRequest.setPlayer2Ships(player2Ships);
        mapper = new ObjectMapper();
    }

    @Test
    public void testSetupGameValid() throws Exception {
        Mockito.when(battleshipService.save(any(BattleShipRequest.class)))
                .thenReturn(ResponseEntity.ok("Game setup successful"));

        mockMvc.perform(post("/api/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(battleShipRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Game setup successful"));
    }

   @Test
    public void testSetupNullPlayerNameRequest() throws Exception {
        battleShipRequest.setPlayer1Name(null);
       mockMvc.perform(post("/api/setup")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(battleShipRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"message\":\"Player1 name cannot be blank\"}"));
    }

    @Test
    public void testSetupWithEmptyPlayerShips() throws Exception {
        battleShipRequest.setPlayer1Ships(null);
        mockMvc.perform(post("/api/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(battleShipRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"message\":\"Player1 Ships cannot be empty\"}"));
    }

    @Test
    public void testSetupWithInvalidShipsData() throws Exception {
        battleShipRequest.getPlayer1Ships().get(0).setType(null);
        battleShipRequest.getPlayer2Ships().get(0).setType(null);
        mockMvc.perform(post("/api/setup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(battleShipRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"message\":\"Ship type cannot be blank, Ship type cannot be blank\"}"));
    }

    @Test
    public void testAttackCellValid() throws Exception {
        Mockito.when(battleshipService.attack(1L, "Player1", "B10"))
                .thenReturn(ResponseEntity.ok("Miss"));

        String position = "B10";

        mockMvc.perform(post("/api/attack")
                        .param("gameId", "1")
                        .param("playerName", "Player1")
                        .param("position", position))
                .andExpect(status().isOk())
                .andExpect(content().string("Miss"));
    }

  @Test
    public void testAttackCellInvalidGameId() throws Exception {
        String position = "A1";

        mockMvc.perform(post("/api/attack")
                        .param("gameId", "1a")
                        .param("playerName", "Player1")
                        .param("position", position))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"message\":\"attackCell.gameId: Only numbers are allowed for GameID\"}"));
    }

    @Test
    public void testAttackCellInvalidPlayerName() throws Exception {
        String position = "A1";

        mockMvc.perform(post("/api/attack")
                        .param("gameId", "1")
                        .param("playerName", "Player1$")
                        .param("position", position))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"message\":\"attackCell.playerName: PlayerName must contain only letters and digits\"}"));
    }

    @Test
    public void testAttackCellInvalidPosition() throws Exception {
        String position = "9Z9";

        mockMvc.perform(post("/api/attack")
                        .param("gameId", "1")
                        .param("playerName", "Player1")
                        .param("position", position))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":400,\"message\":\"attackCell.position: Invalid Attack Position. Use one capital letter followed by two digit number\"}"));
    }
}