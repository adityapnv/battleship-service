package com.abnamro.battleship.domain;

import com.abnamro.battleship.entity.Ship;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class BattleShipRequest {

    @NotBlank(message = "Player1 name cannot be blank")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "PlayerName must contain only letters and digits")
    String player1Name;
    @Valid
    @NotEmpty(message = "Player1 Ships cannot be empty")
    List<Ship> player1Ships;
    @NotBlank(message = "Player2 name cannot be blank")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "PlayerName must contain only letters and digits")
    String player2Name;
    @Valid
    @NotEmpty(message = "Player2 Ships cannot be empty")
    List<Ship> player2Ships;

}
