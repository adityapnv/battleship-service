package com.abnamro.battleship.domain;

import com.abnamro.battleship.entity.Ship;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Data
public class BattleShipRequest {

    String player1Name;
    List<Ship> player1Ships;
    String player2Name;
    List<Ship> player2Ships;

}
