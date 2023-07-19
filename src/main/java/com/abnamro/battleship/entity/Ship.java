package com.abnamro.battleship.entity;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ship")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Type cannot be blank")
    private String type;
    @NotNull(message = "position cannot be blank")
    @Pattern(regexp = "^[A-Z]\\d{1,2}$", message = "Position must contain only one capital letter and two digits max")
    private String position;
    @NotNull(message = "orientation cannot be blank")
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



