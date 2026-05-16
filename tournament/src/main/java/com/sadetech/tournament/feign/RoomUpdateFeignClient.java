package com.sadetech.tournament.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "room-creation", contextId = "updatingRoom")
public interface RoomUpdateFeignClient {
    @PutMapping("/api/room/update-9/tournament/{roomId}")
    void updateRoomForTournament(
            @PathVariable String roomId,
            @RequestParam(required = false) String playerId1,
            @RequestParam(required = false) String playerId2,
            @RequestParam(required = false) String playerId3,
            @RequestParam(required = false) String playerId4,
            @RequestParam(required = false) String playerId5,
            @RequestParam(required = false) String playerId6,
            @RequestParam(required = false) String playerId7,
            @RequestParam(required = false) String playerId8,
            @RequestParam(required = false) String playerId9
    );
}
