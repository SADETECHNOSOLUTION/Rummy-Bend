package com.sadetech.tournament.feign;

import com.sadetech.tournament.dto.RequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-info",contextId = "updateMoney")
public interface UpdateMoneyFeignClient {
    @PostMapping("/api/user/update-money/{playerId}")
    RequestDto updateInGameMoney(@PathVariable String playerId, @RequestParam double inGameMoney);
}