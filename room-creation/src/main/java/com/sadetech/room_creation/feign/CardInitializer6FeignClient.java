package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.CardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "game-engine", contextId = "initializeCardFor6Players")
public interface CardInitializer6FeignClient {

    @PostMapping("/api/cards/initialize-6/{roomId}")
    CardDTO initializeDeckForSixPlayer(@PathVariable String roomId);
}
