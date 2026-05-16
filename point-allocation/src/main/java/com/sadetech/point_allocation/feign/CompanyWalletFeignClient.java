package com.sadetech.point_allocation.feign;

import com.sadetech.point_allocation.dto.Wallet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wallet", contextId = "companyWallet")
public interface CompanyWalletFeignClient {
    @PostMapping("/api/wallet/company-wallet")
    Wallet updateCompanyWallet(@RequestParam String id, @RequestParam double companyWallet);
}
