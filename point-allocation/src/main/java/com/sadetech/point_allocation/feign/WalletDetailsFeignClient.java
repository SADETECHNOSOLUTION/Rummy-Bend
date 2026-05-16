package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "wallet", contextId = "walletDetails")
public interface WalletDetailsFeignClient {
    @GetMapping("/api/wallet/get-wallet/{id}")
    Optional<Wallet> getWalletDetails(@PathVariable String id);
}
