package com.sadetech.tournament.feign;

import com.sadetech.tournament.dto.RequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-info", contextId = "getUser")
public interface UserDetailFeignClient {

        @GetMapping("/api/user/get-user/{playerId}")
        RequestDto getDetails(@PathVariable String playerId);
}
