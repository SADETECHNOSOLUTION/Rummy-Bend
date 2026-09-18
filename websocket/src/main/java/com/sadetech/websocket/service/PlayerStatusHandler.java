package com.sadetech.websocket.service;

import com.sadetech.websocket.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import java.util.Set;

public class PlayerStatusHandler extends TextWebSocketHandler {
    private final MongoTemplate mongoTemplate;
    public static final ConcurrentHashMap<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();   public PlayerStatusHandler(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    public static final ConcurrentHashMap<String, WebSocketSession> onlinePlayers = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PlayerStatusHandler.class);
    private static final long PING_TIMEOUT = TimeUnit.SECONDS.toMillis(30); // Set the timeout to 30 seconds
    public static final ConcurrentHashMap<String, Long> playerLastPingTime = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromSession(session);
        logger.info("Incoming WebSocket URI: {}", session.getUri());
        logger.info("Extracted Token: {}", token);

        // 🛑 TEMPORARILY BYPASS VALIDATION TO TEST SOCKET STABILITY
        String email = "testuser@sadetech.com";
        if (token != null) {
            try {
                email = JwtUtil.validateToken(token).getSubject();
            } catch (Exception e) {
                logger.warn("Token validation failed, but allowing connection for test: {}", e.getMessage());
            }
        }

        logger.info("Player {} successfully connected", email);
        onlinePlayers.put(email, session);
        playerLastPingTime.put(email, System.currentTimeMillis());
        broadcastOnlinePlayers();
    }

    // Add this map at the top of your class alongside your other maps:
// public static final ConcurrentHashMap<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        logger.info("Received message: {}", payload);

        String trimmedPayload = payload.trim();

        if (trimmedPayload.equals("PING")) {
            logger.info("Received PING from client, responding with PONG.");
            session.sendMessage(new TextMessage("PONG"));
        } else if (trimmedPayload.contains("JOINED")) {
            logger.info("Received JOINED message from client for room setup.");

            String gameStatus = "Waiting"; // Default fallback status
            try {
                // Parse incoming JSON to extract the roomId sent by the client
                JsonNode jsonMessage = new ObjectMapper().readTree(payload);
                String roomId = jsonMessage.path("roomId").asText();

                // 🔍 Dynamically query MongoDB 'game_room' collection using MongoTemplate
                if (roomId != null && !roomId.trim().isEmpty()) {
                    Document roomDoc = mongoTemplate.findById(roomId, Document.class, "game_room");
                    if (roomDoc != null && roomDoc.containsKey("gameStatus")) {
                        gameStatus = roomDoc.getString("gameStatus");
                        logger.info("Fetched actual room status from DB for room {}: {}", roomId, gameStatus);
                    }

                    // 🚀 ADD THIS: Map this session to its specific room for live broadcasting
                    roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
                }

                String token = getTokenFromSession(session);
                if (token != null) {
                    String email = JwtUtil.validateToken(token).getSubject();
                    playerLastPingTime.put(email, System.currentTimeMillis());
                }
            } catch (Exception e) {
                logger.error("Error handling JOINED message: {}", e.getMessage());
            }

            // 🚀 Send the true database-backed status back to the client
            String responsePayload = String.format("{\"status\":\"CONNECTED_ACK\", \"gameStatus\":\"%s\"}", gameStatus);
            session.sendMessage(new TextMessage(responsePayload));
        } else {
            logger.info("Received other message: {}", trimmedPayload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        logger.info("WebSocket session closed: {}", session.getId());
        onlinePlayers.entrySet().removeIf(entry -> {
            boolean match = entry.getValue().equals(session);
            if (match) {
                logger.info("Removing player {} from online pool upon leaving", entry.getKey());
                playerLastPingTime.remove(entry.getKey());
            }
            return match;
        });

        // 🚀 ADD THIS: Remove session from the room-specific session tracking map
        roomSessions.values().forEach(sessions -> sessions.remove(session));

        broadcastOnlinePlayers();
    }

//    @Scheduled(fixedRate = 10000) // Check every 10 seconds
//    public void checkForInactivePlayers() {
//        long currentTime = System.currentTimeMillis();
//        playerLastPingTime.forEach((email, lastPingTime) -> {
//            if (currentTime - lastPingTime > PING_TIMEOUT) {
//                logger.warn("Player {} has been inactive for too long, disconnecting.", email);
//                WebSocketSession session = onlinePlayers.get(email);
//                if (session != null && session.isOpen()) {
//                    try {
//                        session.close(CloseStatus.GOING_AWAY);
//                    } catch (IOException e) {
//                        logger.error("Error while closing session for player {}", email, e);
//                    }
//                }
//                onlinePlayers.remove(email);
//                playerLastPingTime.remove(email);
//                broadcastOnlinePlayers();
//            }
//        });
//    }

    public static void broadcastToRoom(String roomId, String gameStatus) {
        Set<WebSocketSession> sessions = roomSessions.get(roomId);
        if (sessions != null) {
            String payload = String.format("{\"status\":\"ROOM_UPDATE\", \"gameStatus\":\"%s\"}", gameStatus);
            TextMessage message = new TextMessage(payload);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(message);
                    } catch (IOException e) {
                        logger.error("Failed to broadcast to session {}", session.getId(), e);
                    }
                }
            }
        }
    }

    private void broadcastOnlinePlayers() {
            onlinePlayers.forEach((email, session) -> {
                Long lastPing = playerLastPingTime.get(email);
                logger.info("Player {} is online, last ping time: {}", email, lastPing);
            });
            logger.info("Total Online Players: {}", onlinePlayers.size());
    }

    private String getTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.contains("token=")) {
            return query.split("token=")[1];
        }
        return null;
    }

    @Scheduled(fixedRate = 30000) // Run every 30 seconds
    public void removeInactivePlayers() {
        onlinePlayers.forEach((email, session) -> {
            if (!session.isOpen()) {
                logger.info("Removing inactive player: {}", email);
                onlinePlayers.remove(email);
            }
        });
        broadcastOnlinePlayers();
    }

}
