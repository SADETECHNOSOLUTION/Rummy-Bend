package com.sadetech.point_allocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "point_allocation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    @Id
    private String id;
    private String roomId;
    private Map<String, PlayerData> players;
    private int round;
    private String gameStatus;
    private String declaredPlayerId;
    private int exceedPoint;
    private List<String> activePlayers;
}
