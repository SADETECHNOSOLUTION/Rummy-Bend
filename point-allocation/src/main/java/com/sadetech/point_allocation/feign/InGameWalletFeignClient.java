package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wallet", contextId = "inGameWallet")
public interface InGameWalletFeignClient {
    @PostMapping("/api/wallet/in-game-wallet")
    Wallet updateInGameWallet(@RequestParam String id, @RequestParam double inGameWallet);
}
