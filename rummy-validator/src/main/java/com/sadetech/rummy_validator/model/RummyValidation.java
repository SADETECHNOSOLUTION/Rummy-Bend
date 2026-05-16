package com.sadetech.rummy_validator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Document(collection = "validator")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RummyValidation {
    @Id
    private String id;
    private String roomId;
    private Map<String, PlayerData> players;
    private Map<String, Map<String, List<String>>> playersCards;
    private Map<String, Double> playerTotalPoints;
    private Map<String, String> playerDropType;
    @CreatedDate
    private LocalDateTime validatedAt;

}
