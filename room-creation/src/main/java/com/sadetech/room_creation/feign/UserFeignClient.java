package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.RequestDTO;
import com.sadetech.room_creation.dto.WalletDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "user-info", contextId = "getUser")
public interface UserFeignClient {

    @GetMapping("/api/user/get-user/{playerId}")
    Optional<RequestDTO> getDetails(@PathVariable String playerId);

    @PostMapping("/api/user/add-transaction")
    String wallet (@RequestBody WalletDto wallet);

}
