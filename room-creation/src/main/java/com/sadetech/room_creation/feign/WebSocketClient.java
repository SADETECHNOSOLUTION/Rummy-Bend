package com.sadetech.room_creation.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "websocket-application", url = "http://localhost:7080")
public interface WebSocketClient {

    @PostMapping("/internal/websocket/broadcast")
    void broadcastToRoom(@RequestParam("roomId") String roomId, @RequestParam("gameStatus") String gameStatus);
}