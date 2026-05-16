package com.sadetech.game_engine.controller;

import com.sadetech.game_engine.dto.CardDistributionResponse;
import com.sadetech.game_engine.dto.RankPlayersDto;
import com.sadetech.game_engine.model.Card;
import com.sadetech.game_engine.model.CardUpdateLog;
import com.sadetech.game_engine.model.Points;
import com.sadetech.game_engine.service.CardLoggingService;
import com.sadetech.game_engine.service.CardService;
import com.sadetech.game_engine.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private CardLoggingService cardLoggingService;

    @Autowired
    private PointService pointService;

    @PostMapping("/initialize-6/{roomId}")
    public Card initializeDeckForSixPlayer(@PathVariable String roomId) {
        return cardService.initializeDeckForSixPlayer(roomId);
    }

    @PostMapping("/initialize-9/{roomId}")
    public Card initializeDeckForNinePlayer(@PathVariable String roomId) {
        return cardService.initializeDeckForNinePlayer(roomId);
    }

    @GetMapping("/{roomId}/distribute")
    public ResponseEntity<CardDistributionResponse> getCardDetails(@PathVariable String roomId) {

            CardDistributionResponse response = cardService.getCardDetails(roomId);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
    }

    @GetMapping("/{roomId}/rank-players")
    public ResponseEntity<RankPlayersDto> rankPlayers(@PathVariable String roomId) {
            // Call service to get player rankings and 0th index cards
            RankPlayersDto result = cardService.rankPlayersAndGetCards(roomId);

            // Return the result in the response
            return ResponseEntity.ok(result);
    }

    @GetMapping("/get-card/{roomId}")
    public ResponseEntity<?> getCardDetailsByRoomId(@PathVariable String roomId){
        Card card = cardService.getCardDetailsByRoomId(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(card);
    }

    @PutMapping("/{roomId}/update")
    public ResponseEntity<Card> updatePlayerCardOnHisTurn(
            @PathVariable String roomId,
            @RequestParam String playerId,
            @RequestParam (required = false) Boolean pickFromDiscarded,
            @RequestParam(required = false) String cardToDiscard,
            @RequestParam(required = false) String otherPlayerId,
            @RequestHeader("x-player-id") String extractedId) {

            Card updatedCard = cardService.updatePlayerCardOnHisTurn(roomId, playerId, pickFromDiscarded, cardToDiscard, otherPlayerId, extractedId);

            cardLoggingService.logUpdateAsNewDocument(roomId, playerId, pickFromDiscarded, cardToDiscard, otherPlayerId, updatedCard);

            return ResponseEntity.ok(updatedCard);
    }

    @GetMapping("/group-by-suit/{roomId}")
    public ResponseEntity<?> getCardDetailsBySuit(@PathVariable String roomId){

            Map<String, Map<String, List<Map<String, String>>>> card = cardService.groupCardForPlayersUsingSuit(roomId);
            return ResponseEntity.ok(card);

    }

    @GetMapping("/get-log/{roomId}")
    public ResponseEntity<?> getCardLogDetails(@PathVariable String roomId){

            List<CardUpdateLog> cardUpdateLogList = cardLoggingService.getCardDetails(roomId);
            return ResponseEntity.status(HttpStatus.CREATED).body(cardUpdateLogList);
    }

    @PostMapping("/points")
    public Points addPointsToGame(@RequestBody Points points){
        return pointService.addDetails(points);
    }

    @GetMapping("/get-points")
    public ResponseEntity<Points> getPointsDetails(@RequestParam int playerCount, @RequestParam String type, @RequestParam int defaultValue){

            Points point = pointService.getPoints(playerCount,type,defaultValue);
            return ResponseEntity.ok(point);
    }

    @PutMapping("/update-points")
    public ResponseEntity<Points> updatePointValue(
            @RequestParam int playerCount,
            @RequestParam String type,
            @RequestParam int defaultValue,
            @RequestParam int id,
            @RequestParam double pointValue,
            @RequestParam double money) {

        Points updatedPoints = pointService.updatePointValue(playerCount, type, defaultValue, id, pointValue, money);
        return ResponseEntity.ok(updatedPoints);
    }

    @DeleteMapping("/delete-point")
    public ResponseEntity<?> deletePoint(
            @RequestParam int playerCount,
            @RequestParam String type,
            @RequestParam int defaultValue,
            @RequestParam int id) {

        Points updatedPoints = pointService.deletePointValue(playerCount, type, defaultValue, id);

        return ResponseEntity.ok(Objects.requireNonNullElse(updatedPoints, "PointValue deleted and Points document removed."));
    }

    @GetMapping("/grouped")
    public ResponseEntity<Map<String, Object>> getGroupedCards(
            @RequestParam String roomId,
            @RequestParam String playerId) {

        Map<String, Object> response = cardService.getGroupedCardsByRoomAndPlayerCustomFormat(roomId, playerId);
        return ResponseEntity.ok(response);
    }

}