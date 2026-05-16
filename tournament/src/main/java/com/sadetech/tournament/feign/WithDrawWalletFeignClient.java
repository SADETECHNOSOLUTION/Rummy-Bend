package com.sadetech.tournament.feign;

import com.sadetech.tournament.dto.WalletDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wallet", contextId = "withDrawWalletUpdater")
public interface WithDrawWalletFeignClient {

    @PostMapping("/api/wallet/withdraw-wallet")
    WalletDTO updateWithDrawWallet(@RequestParam String id, @RequestParam double withDrawWallet);
}
