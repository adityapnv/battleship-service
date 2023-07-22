package com.abnamro.battleship;

import com.abnamro.battleship.controller.BattleshipController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BattleShipApplicationTests {

	@Autowired
	BattleshipController battleshipController;

	@Test
	void contextLoads(){
		Assertions.assertThat(battleshipController).isNotNull();
	}

}
