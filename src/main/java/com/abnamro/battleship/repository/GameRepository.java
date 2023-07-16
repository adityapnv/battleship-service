package com.abnamro.battleship.repository;

import com.abnamro.battleship.entity.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {
}

