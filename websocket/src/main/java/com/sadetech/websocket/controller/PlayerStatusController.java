package com.sadetech.websocket.controller;

import com.sadetech.websocket.service.PlayerStatusHandler;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/player-status")
public class PlayerStatusController {

    @PostMapping("/broadcast/{roomId}")
    public ResponseEntity<Void> broadcastCardUpdate(
            @PathVariable String roomId,
            @RequestBody String jsonPayload) {

        // This triggers your WebSocket service's static broadcast method safely within its own JVM
        PlayerStatusHandler.broadcastToRoom(roomId, jsonPayload);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/online-players")
    public Set<String> getOnlinePlayers() {
        return PlayerStatusHandler.onlinePlayers.keySet();
    }

    @GetMapping("/online-count")
    public int getOnlinePlayersCount() {
        return PlayerStatusHandler.onlinePlayers.size();
    }

    @GetMapping("/last-ping-time/{email}")
    public Map<String, Long> lastPingTime(@PathVariable String email) {
        Long lastPing = PlayerStatusHandler.playerLastPingTime.get(email);
        if (lastPing != null) {
            return Map.of("lastPingTime", lastPing);
        } else {
            return Map.of("error", -1L);
        }
    }
}

// Standalone controller so Feign can reach /internal/websocket/broadcast cleanly
@RestController
@RequestMapping("/internal/websocket")
class WebSocketInternalController {

    @PostMapping("/broadcast")
    public ResponseEntity<String> triggerBroadcast(@RequestParam String roomId, @RequestParam String gameStatus) {
        PlayerStatusHandler.broadcastToRoom(roomId, gameStatus);
        return ResponseEntity.ok("Broadcast sent");
    }
}