package com.sadetech.rummy_validator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.time.LocalDateTime;

@Document(collection = "validation_responses") // Store responses separately
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RummyValidationResponse {
    @Id
    private String id;
    private String roomId;
    private List<PlayerScore> playerScores;
    private double totalLostPoints;
    private LocalDateTime validatedAt;
}
