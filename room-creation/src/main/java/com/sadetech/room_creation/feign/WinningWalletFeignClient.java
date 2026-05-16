package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.RequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-info", contextId = "winningWallet")
public interface WinningWalletFeignClient {
    @PostMapping("/api/user/update-winning-money/{playerId}")
    RequestDTO updateWinningMoney(@PathVariable String playerId, @RequestParam double money);
}
