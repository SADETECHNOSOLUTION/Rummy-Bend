package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wallet", contextId = "withDrawWallet")
public interface WithDrawMoneyFeignClient {

    @PostMapping("/api/wallet/withdraw-wallet")
    Wallet updateWithDrawWallet(@RequestParam String id, @RequestParam double withDrawWallet);
}
