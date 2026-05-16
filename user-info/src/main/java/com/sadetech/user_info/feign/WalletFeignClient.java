package com.sadetech.user_info.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @FeignClient(name = "wallet", contextId = "inGameMoneyUpdate")
public interface WalletFeignClient {

}
