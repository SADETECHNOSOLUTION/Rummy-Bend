package com.sadetech.tournament.feign;

import com.sadetech.tournament.dto.PointDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "point-allocation")
public interface PointFeignClient {
    @GetMapping("/api/point/result/{roomId}")
    PointDto getResult(@PathVariable String roomId, @RequestParam String gameStatus);
}
