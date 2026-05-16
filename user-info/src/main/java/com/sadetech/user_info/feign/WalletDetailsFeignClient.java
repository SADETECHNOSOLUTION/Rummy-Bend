package com.sadetech.user_info.feign;

import com.sadetech.user_info.dto.WalletDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

// @FeignClient(name = "wallet", contextId = "walletDetails")
public interface WalletDetailsFeignClient {

}