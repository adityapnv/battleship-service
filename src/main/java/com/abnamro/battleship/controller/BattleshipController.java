package com.abnamro.battleship.controller;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.service.BattleshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/battleship")
@Validated
public class BattleshipController {

    private final BattleshipService battleshipService;

    public BattleshipController(BattleshipService battleshipService) {
        this.battleshipService = battleshipService;
    }

    /**
     * endpoint to do initial setup of the game.
     * @param battleShipRequest players and ships details
     * @return gameID.
     */
    @PostMapping("/setup")
    public ResponseEntity<String> setupGame(@Valid @RequestBody BattleShipRequest battleShipRequest) {
        return battleshipService.save(battleShipRequest);

    }

    /**
     * Endpoint to play the game and attack the opponent.
     * @param gameId Game ID
     * @param playerName name of the attacking player
     * @param position where to attack.
     * @return Hit or Miss or Sunk or winner.
     */
    @PutMapping("/attack")
    public ResponseEntity<String> attackCell(
            @NotBlank(message = "gameId cannot be empty")
            @Pattern(regexp = "^[0-9]+$", message = "Only numbers are allowed for GameID")
            @RequestParam String gameId,
            @NotBlank(message = "playerName cannot be blank")
            @Pattern(regexp = "[A-Za-z0-9]+", message = "PlayerName must contain only letters and digits")
            @RequestParam String playerName,
            @NotBlank(message = "position cannot be blank")
            @Pattern(regexp = "^[A-Z]\\d{1,2}$", message = "Invalid Attack Position. Use one capital letter followed by two digit number")
            @RequestParam String position) {
        return battleshipService.attack(Long.valueOf(gameId),playerName, position);

    }


}
