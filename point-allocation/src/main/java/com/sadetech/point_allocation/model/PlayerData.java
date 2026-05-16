package com.sadetech.point_allocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
@Document(collection = "points")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    private String totalScore; // Total points assigned
    private String playerRemainingScore; // Remaining points
    private String playerLostScore; // Lost points
}