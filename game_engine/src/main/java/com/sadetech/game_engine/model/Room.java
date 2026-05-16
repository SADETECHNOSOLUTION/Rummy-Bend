package com.sadetech.game_engine.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.sadetech.game_engine.dto.PlayerDetails; // FIX: Import the missing symbol
import java.util.List;

@Data
@NoArgsConstructor // Required for MongoDB and empty initializations
@AllArgsConstructor // Required for full-argument object creation
@Document(collection = "rooms")
public class Room {
    @Id
    private String id;
    private String roomId;
    private String gameStatus;
    private String currentTurn;
    private List<PlayerDetails> playerDetails;
}