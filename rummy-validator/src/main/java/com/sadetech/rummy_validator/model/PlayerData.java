package com.sadetech.rummy_validator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    private Map<String, List<String>> sequenceCard; // Stores sequences: pureSequence, set, etc.
    private String totalScore; // Total points assigned
    private String playerRemainingScore; // Remaining points
    private String playerLostScore; // Lost points
}