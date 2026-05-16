package com.sadetech.point_allocation.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RejoinService {
    private final Map<String, Boolean> rejoinDecisions = new ConcurrentHashMap<>();

    public void updateRejoinDecision(String playerId, String roomId, boolean decision) {
        rejoinDecisions.put(playerId + "_" + roomId, decision);
    }

    public boolean getRejoinDecision(String playerId, String roomId) {
        return rejoinDecisions.getOrDefault(playerId + "_" + roomId, false);
    }
}
