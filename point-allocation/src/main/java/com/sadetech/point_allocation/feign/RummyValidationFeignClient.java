package com.sadetech.point_allocation.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "rummy-validator")
public interface RummyValidationFeignClient {
    
    @GetMapping("/api/rummy/get/{roomId}")
    Map<String, Object> getValidationResult(@PathVariable("roomId") String roomId);
}
