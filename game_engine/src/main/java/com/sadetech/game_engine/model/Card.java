package com.sadetech.game_engine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
// ... other imports

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cards")
public class Card {
    @Id
    private String id;
    private List<Map<String, String>> playerDetails;
    private String roomId;
    private String jokerCard;
    
    // Updated specific types to fix "incompatible types"
    private Map<String, Integer> playerSteps; 
    private List<Map<String, String>> remainingCards;
    private Map<String, List<Map<String, String>>> discardedCards;
    private List<Map<String, String>> allDiscardedCards;
    
    private String currentTurn;
    
    // Corrected type for grouped cards
    private Map<String, Map<String, List<Map<String, String>>>> groupedCardsBySuit;
    
    private Map<String, List<Map<String, String>>> playerCards;
    private List<String> orderPlayersByRank;

}