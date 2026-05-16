package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.RoomDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "price-update")
public interface RoomPriceUpdateFeignClient {

    @PutMapping("/api/room/update-totalPrice")
    RoomDto updateTotalPrice(@RequestParam String roomId, @RequestParam double totalPrice);
}
