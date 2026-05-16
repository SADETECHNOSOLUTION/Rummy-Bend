package com.sadetech.game_engine.feign;

import com.sadetech.game_engine.dto.RoomDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.OptionsFactoryBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

// @FeignClient(name = "room-creation")
public interface RoomFeignClient {
    @GetMapping("/api/room/get-detail/{roomId}")
    Optional<RoomDTO> getRoomDetails(@PathVariable String roomId);

    @PutMapping("/api/room/update-current-turn/{roomId}")
    RoomDTO updatePlayerTurn(@PathVariable String roomId, @RequestParam String playerId);
}
