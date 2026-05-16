package com.sadetech.room_creation.feign;

import com.sadetech.room_creation.dto.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "wallet", contextId = "walletDetails")
public interface WalletDetailsFeignClient {
    @GetMapping("/api/wallet/get-wallet/{id}")
    Optional<Wallet> getWalletDetails(@PathVariable String id);
}
