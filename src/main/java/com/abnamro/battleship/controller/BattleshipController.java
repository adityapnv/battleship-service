package com.abnamro.battleship.controller;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.service.BattleshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/api")
public class BattleshipController {

    private final BattleshipService battleshipService;

    public BattleshipController(BattleshipService battleshipService) {
        this.battleshipService = battleshipService;
    }

    @PostMapping("/setup")
    public ResponseEntity<String> setupGame(@Valid @RequestBody BattleShipRequest battleShipRequest) {

        return battleshipService.save(battleShipRequest);

    }

    @PostMapping("/attack")
    public ResponseEntity<String> attackCell(
            @NotNull(message = "gameId cannot be blank")
            @Pattern(regexp = "^[0-9]+$", message = "Only numbers are allowed for GameID")
            @RequestParam Long gameId,
            @NotNull(message = "playerName cannot be blank")
            @Pattern(regexp = "[A-Za-z0-9]+", message = "PlayerName must contain only letters and digits")
            @RequestParam String playerName,
            @NotNull(message = "position cannot be blank")
            @Pattern(regexp = "^[A-J][1-9]|10$", message = "Invalid format for Position. Use one character from A to J and numbers 1 to 10.")
            @RequestParam String position) {
        return battleshipService.attack(gameId,playerName, position);

    }


}
