package com.abnamro.battleship.repository;

import com.abnamro.battleship.entity.Ship;
import org.springframework.data.repository.CrudRepository;

public interface ShipRepository extends CrudRepository<Ship, Long> {
}

