package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.RequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-info",contextId = "updateMoney")
public interface  UpdateMoneyFeignClient {
    @PostMapping("/api/user/update-money/{playerId}")
    RequestDTO updateInGameMoney(@PathVariable String playerId, @RequestParam double inGameMoney);
}
