package com.abnamro.battleship;

import com.abnamro.battleship.domain.BattleShipRequest;
import com.abnamro.battleship.entity.Ship;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BattleShipApplicationTests {

	@Test
	void contextLoads() throws JsonProcessingException {

		BattleShipRequest battleShipRequest = new BattleShipRequest();
		List<Ship> player1Ships = new ArrayList<>();
		Ship s1 = new Ship();
		s1.setType("A");
		s1.setPosition("A1");
		s1.setOrientation("Horizontal");
		s1.setSize(3);
		Ship s2 = new Ship();
		s2.setType("B");
		s2.setPosition("B2");
		s2.setOrientation("Horizontal");
		s2.setSize(3);
		player1Ships.add(s1);
		player1Ships.add(s2);
		battleShipRequest.setPlayer1Ships(player1Ships);
		battleShipRequest.setPlayer2Ships(player1Ships);

		System.out.println(player1Ships);

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(battleShipRequest);
		System.out.println(json);

	}

}
