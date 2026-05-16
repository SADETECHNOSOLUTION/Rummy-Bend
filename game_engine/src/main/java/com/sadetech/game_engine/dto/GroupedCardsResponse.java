package com.sadetech.game_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupedCardsResponse {
    private String roomId;
    private String playerId;
    private List<List<Map<String, String>>> groupedCards;
}
