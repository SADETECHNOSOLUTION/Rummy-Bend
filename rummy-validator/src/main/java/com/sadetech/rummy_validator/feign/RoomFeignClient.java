package com.sadetech.rummy_validator.feign;

import com.sadetech.rummy_validator.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-creation")
public interface RoomFeignClient {
    @GetMapping("/api/room/get-detail/{roomId}")
    RoomDto getRoomDetails(@PathVariable String roomId);
}
