package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.CardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "game-engine", contextId = "initializeCardFor9Players")
public interface CardInitializer9FeignClient {

    @PostMapping("/api/cards/initialize-9/{roomId}")
    CardDTO initializeDeckForNinePlayer(@PathVariable String roomId);
}
