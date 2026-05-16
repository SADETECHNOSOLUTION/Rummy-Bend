package com.sadetech.room_creation.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-info", contextId = "updateCashGameWalletAndLoyaltyPoint")
public interface UpdateCashGameFeignClient {

    @PutMapping("/api/user/update-cash-game-wallet/{playerId}")
    void updateCashGameWalletAndLoyaltyPoint(@PathVariable String playerId, @RequestParam double cashGameWallet);
}
