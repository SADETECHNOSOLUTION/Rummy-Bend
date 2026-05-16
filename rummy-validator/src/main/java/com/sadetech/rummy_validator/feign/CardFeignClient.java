package com.sadetech.rummy_validator.feign;

import com.sadetech.rummy_validator.dto.CardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "game-engine")
public interface CardFeignClient {
    @GetMapping("/api/cards/get-card/{roomId}")
    Optional<CardDTO> getCardDetailsByRoomId(@PathVariable String roomId);
}
