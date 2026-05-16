package com.sadetech.user_info.feign;

import com.sadetech.user_info.dto.WalletDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// @FeignClient(name = "wallet", contextId = "withDrawWalletUpdater")
public interface WithDrawWalletFeignClient {

}
