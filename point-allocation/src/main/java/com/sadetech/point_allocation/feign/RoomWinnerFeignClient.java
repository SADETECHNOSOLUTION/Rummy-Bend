package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "room-creation", contextId = "updateWinner")
public interface RoomWinnerFeignClient {
    @PostMapping("/api/room/update-match-winner/{roomId}")
    RoomDto updateGameWinner(@PathVariable("roomId") String roomId, @RequestParam("matchWinner") String matchWinner);
}
