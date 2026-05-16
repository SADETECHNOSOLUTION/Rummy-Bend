package com.sadetech.game_engine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadetech.game_engine.model.Card;
import com.sadetech.game_engine.model.CardUpdateLog;
import com.sadetech.game_engine.repository.CardUpdateLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CardLoggingService {

    @Autowired
    private CardUpdateLogRepository cardUpdateLogRepository;

    public void logUpdateAsNewDocument(String roomId, String playerId, Boolean pickFromDiscarded, String cardToDiscard, String otherPlayerId, Card updatedCard) {
        // Create a new log object with the Card object
        CardUpdateLog log = new CardUpdateLog(roomId, playerId, pickFromDiscarded, cardToDiscard, otherPlayerId, updatedCard);

        // Save the log to the database
        cardUpdateLogRepository.save(log);
    }

    public List<CardUpdateLog> getCardDetails(String roomId){
        return cardUpdateLogRepository.findByRoomId(roomId);
    }
}

