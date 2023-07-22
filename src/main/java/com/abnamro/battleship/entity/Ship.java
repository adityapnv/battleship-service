package com.abnamro.battleship.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ship")
public class Ship {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Ship type cannot be blank")
    private String type;
    @NotBlank(message = "position cannot be blank")
    @Pattern(regexp = "^[A-Z]\\d{1,2}$", message = "Position must contain one capital letter followed by two digit number")
    private String position;
    @NotBlank(message = "orientation cannot be blank")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Only strings are allowed for orientation.")
    private String orientation;
    @JsonIgnore
    @ElementCollection
    private List<String> positions;
    @JsonIgnore
    @ElementCollection
    private List<String> hitPositions;

    public Ship() {
        this.positions = new ArrayList<>();
        this.hitPositions = new ArrayList<>();
    }
}



