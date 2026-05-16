package com.sadetech.game_engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "card_update_logs")
public class CardUpdateLog {
    @Id
    private String id;
    private String roomId;
    private String playerId;
    private Boolean pickFromDiscarded;
    private String cardToDiscard;
    private String otherPlayerId;
    private Card updatedCard;

    @Indexed
    private Date timestamp;

    public CardUpdateLog(String roomId, String playerId, Boolean pickFromDiscarded, String cardToDiscard, String otherPlayerId, Card updatedCard) {
        this.roomId = roomId;
        this.playerId = playerId;
        this.pickFromDiscarded = pickFromDiscarded;
        this.cardToDiscard = cardToDiscard;
        this.otherPlayerId = otherPlayerId;
        this.updatedCard = updatedCard;
        this.timestamp = new Date();
    }
}
