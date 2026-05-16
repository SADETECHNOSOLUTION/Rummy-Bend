package com.sadetech.tournament.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-info", contextId = "user-exist")
public interface UserFeignClient {
    @GetMapping("/api/user/player-exist/{playerId}")
    boolean findUserExistOrNot(@PathVariable String playerId);
}
