package com.sadetech.game_engine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.sadetech.game_engine.dto.PlayerDetails;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "game_room")
public class Room {
    @Id
    private String id;
    private String roomId;
    private Integer roomSize;
    private String roomType;
    private String gameMode;
    private String gameStatus;
    private Integer issuedPoint;
    private Integer playerCount;
    private Integer totalRounds;
    private List<PlayerDetails> playerDetails;
    private String matchWinner;
    private String entryType;
    private Double entryPrice;
    private Double totalPrice;
    private Integer currentRound;
    private String currentTurn;
    private List<String> exitPlayer;
    private List<String> isNotActive;
    private List<String> lastGame;
    private String roomCreatedAt;
    private String tournamentId;
    private String visibility;
}