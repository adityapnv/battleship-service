package com.abnamro.battleship.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Ship> fleet;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Cell> cells;

}
