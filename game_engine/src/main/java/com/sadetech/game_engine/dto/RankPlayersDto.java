package com.sadetech.game_engine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankPlayersDto {

    private List<String> ranking;
    private Map<String,String> playerCards;
    private List<String> orderPlayersByRank;
}
