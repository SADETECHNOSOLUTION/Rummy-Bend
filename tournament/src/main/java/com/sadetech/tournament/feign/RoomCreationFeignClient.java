package com.sadetech.tournament.feign;

import com.sadetech.tournament.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "room-creation", contextId = "creatingRoom")
public interface RoomCreationFeignClient {
    @PostMapping("/api/room/create-room")
    RoomDto createRoom(@RequestBody RoomDto room);
}

