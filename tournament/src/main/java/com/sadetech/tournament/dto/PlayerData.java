package com.sadetech.tournament.dto;

import lombok.Data;

@Data
public class PlayerData {
    private String totalScore; // Total points assigned
    private String playerRemainingScore; // Remaining points
    private String playerLostScore; // Lost points
}