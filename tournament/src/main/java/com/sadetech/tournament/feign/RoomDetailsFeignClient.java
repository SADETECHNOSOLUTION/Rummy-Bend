package com.sadetech.tournament.feign;

import com.sadetech.tournament.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-creation", contextId = "roomDetails")
public interface RoomDetailsFeignClient {
    @GetMapping("/get-detail/{roomId}")
    RoomDto getRoomDetails(@PathVariable String roomId);
}