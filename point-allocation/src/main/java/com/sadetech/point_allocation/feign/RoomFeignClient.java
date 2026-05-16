package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "room-creation", contextId = "getDetails")
public interface RoomFeignClient {
    @GetMapping("/api/room/get-detail/{roomId}")
    RoomDto getRoomDetails(@PathVariable String roomId);
}
