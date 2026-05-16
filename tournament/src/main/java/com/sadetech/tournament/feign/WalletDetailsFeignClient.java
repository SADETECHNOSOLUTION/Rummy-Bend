package com.sadetech.tournament.feign;

import com.sadetech.tournament.dto.WalletDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "wallet", contextId = "walletDetails")
public interface WalletDetailsFeignClient {
    @GetMapping("/api/wallet/get-wallet/{id}")
    Optional<WalletDTO> getWalletDetails(@PathVariable String id);
}