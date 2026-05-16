package com.sadetech.missions.feign;

import com.sadetech.missions.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "user-info")
public interface UserFeignClient {

    @GetMapping("/api/user/get-user/{playerId}")
    Optional<User> getDetails(@PathVariable String playerId);
}
