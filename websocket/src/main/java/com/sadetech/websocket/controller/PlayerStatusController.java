package com.sadetech.websocket.controller;

import com.sadetech.websocket.service.PlayerStatusHandler;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/player-status")
public class PlayerStatusController {

    @GetMapping("/online-players")
    public Set<String> getOnlinePlayers() {
        return PlayerStatusHandler.onlinePlayers.keySet(); // Return list of online player emails
    }

    @GetMapping("/online-count")
    public int getOnlinePlayersCount() {
        return PlayerStatusHandler.onlinePlayers.size();
    }

    @GetMapping("/last-ping-time/{email}")
    public Map<String, Long> lastPingTime(@PathVariable String email) {
        // Fetch the last ping time for the given email
        Long lastPing = PlayerStatusHandler.playerLastPingTime.get(email);
        if (lastPing != null) {
            return Map.of("lastPingTime", lastPing);
        } else {
            // Return a map with null or a sentinel value for missing player
            return Map.of("error", -1L); // Or you can return a custom error code
        }
    }

}
