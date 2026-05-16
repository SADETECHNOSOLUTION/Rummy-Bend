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

public class PlayerStatusHandler extends TextWebSocketHandler {
    public static final ConcurrentHashMap<String, WebSocketSession> onlinePlayers = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(PlayerStatusHandler.class);
    private static final long PING_TIMEOUT = TimeUnit.SECONDS.toMillis(30); // Set the timeout to 30 seconds
    public static final ConcurrentHashMap<String, Long> playerLastPingTime = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromSession(session);
        if (token == null || !JwtUtil.isTokenValid(token)) {
            logger.error("Invalid or missing token. Closing session.");
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        String email = JwtUtil.validateToken(token).getSubject();
        logger.info("Player {} connected", email);
        onlinePlayers.put(email, session);
        broadcastOnlinePlayers();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        logger.info("Received message: {}", payload);  // Log the incoming message

        if (payload.trim().equals("PING")) {
            logger.info("Received PING from client, responding with PONG.");
            session.sendMessage(new TextMessage("PONG"));
            String email = JwtUtil.validateToken(getTokenFromSession(session)).getSubject();
            playerLastPingTime.put(email, System.currentTimeMillis());  // Update last ping time
        } else {
            logger.info("Received unrecognized message: {}", payload);  // Log any other message
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        onlinePlayers.values().remove(session);
        broadcastOnlinePlayers();
    }

    @Scheduled(fixedRate = 10000) // Check every 10 seconds
    public void checkForInactivePlayers() {
        long currentTime = System.currentTimeMillis();
        playerLastPingTime.forEach((email, lastPingTime) -> {
            if (currentTime - lastPingTime > PING_TIMEOUT) {
                logger.warn("Player {} has been inactive for too long, disconnecting.", email);
                WebSocketSession session = onlinePlayers.get(email);
                if (session != null && session.isOpen()) {
                    try {
                        session.close(CloseStatus.GOING_AWAY);
                    } catch (IOException e) {
                        logger.error("Error while closing session for player {}", email, e);
                    }
                }
                onlinePlayers.remove(email);
                playerLastPingTime.remove(email);
                broadcastOnlinePlayers();
            }
        });
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
