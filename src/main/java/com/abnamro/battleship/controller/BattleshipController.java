package com.abnamro.battleship.controller;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Game;
import com.abnamro.battleship.service.BattleshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BattleshipController {

    @Autowired
    private BattleshipService battleshipService;
    private Game game;

    @PostMapping("/setup")
    public ResponseEntity<String> setupGame(@RequestBody BattleShipRequest battleShipRequest) {

        return battleshipService.save(battleShipRequest);

    }

}