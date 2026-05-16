package com.sadetech.rummy_validator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerScore {
    private String playerId;
    private double totalScore;
    private double lostPoints;
    private double remainingPoints;
}
