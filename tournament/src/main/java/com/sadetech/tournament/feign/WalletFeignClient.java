package com.sadetech.tournament.feign;


import com.sadetech.tournament.dto.WalletDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wallet", contextId = "inGameMoneyUpdate")
public interface WalletFeignClient {

    @PostMapping("/api/wallet/in-game-wallet")
    WalletDTO updateInGameWallet(@RequestParam String id, @RequestParam double inGameWallet);
}
