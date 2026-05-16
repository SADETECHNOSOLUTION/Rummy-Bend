package com.sadetech.tournament.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PointDto {
    private String id;
    private String roomId;
    private Map<String, PlayerData> players;
    private int round;
    private String gameStatus;
    private String declaredPlayerId;
}
