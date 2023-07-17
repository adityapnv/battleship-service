package com.abnamro.battleship.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ship")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type;
    private int size;
    private String position;
    private String orientation;
    @ElementCollection
    private List<String> positions;

    @ElementCollection
    private List<String> hitPositions;

    public Ship() {
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
    }
}



