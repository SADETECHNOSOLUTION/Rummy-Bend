package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wallet", contextId = "inGameWallet")
public interface InGameWalletFeignClient {
    @PostMapping("/api/wallet/in-game-wallet")
    Wallet updateInGameWallet(@RequestParam String id, @RequestParam double inGameWallet);
}
