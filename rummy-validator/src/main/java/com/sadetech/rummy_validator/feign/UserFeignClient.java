package com.sadetech.rummy_validator.feign;

import com.sadetech.rummy_validator.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "user-info")
public interface UserFeignClient {

    @GetMapping("/api/user/get-user/{playerId}")
    Optional<UserDto> getDetails(@PathVariable String playerId);

}