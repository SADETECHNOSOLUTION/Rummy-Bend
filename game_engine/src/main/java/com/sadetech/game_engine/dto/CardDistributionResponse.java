package com.sadetech.game_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDistributionResponse {
    private String id;

    private String jokerCard;
    private String roomId;
    private Map<String, List<Map<String, String>>> playerCards = new HashMap<>(); // Cards distributed to players
    private List<Map<String, String>> remainingCards = new ArrayList<>(); // Remaining cards after distribution
    private Map<String, List<Map<String, String>>> discardedCards = new HashMap<>(); // Discarded cards by player
    private List<Map<String, String>> allDiscardedCards = new ArrayList<>(); // General discard pile
    private String declaredCard;

    // Updated field to handle grouped cards by suit for each player
    private Map<String, Map<String, List<Map<String, String>>>> groupedCardsBySuit;

    private Map<String, Integer> playerSteps = new HashMap<>();

}
