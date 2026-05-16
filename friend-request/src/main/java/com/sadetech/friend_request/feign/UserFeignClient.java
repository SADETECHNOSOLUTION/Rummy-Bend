package com.sadetech.friend_request.feign;

import com.sadetech.friend_request.configuration.FeignClientConfig;
import com.sadetech.friend_request.feignDto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "user-info", configuration = FeignClientConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/user/get-user/{playerId}")
    Optional<UserDto> getDetails(@PathVariable String playerId);

}

