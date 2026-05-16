package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.RequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-info", contextId = "getUser")
public interface UserFeignClient {
    @GetMapping("/api/user/get-user/{playerId}")
    RequestDTO getDetails(@PathVariable String playerId);


}
