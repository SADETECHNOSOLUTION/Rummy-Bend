package com.sadetech.room_creation.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import java.util.List;

@FeignClient(name = "point-allocation")
public interface PointGameClient {

    @GetMapping("/api/point/result/{roomId}")
    List<Map<String, Object>> getPointGameResult(@PathVariable String roomId, 
                                                 @RequestParam("gameStatus") String gameStatus);
}
