package com.sadetech.wallet.controller;

import com.sadetech.wallet.model.Wallet;
import com.sadetech.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/create")
    public Wallet createWallet(@RequestBody Wallet wallet){
        return walletService.createWallet(wallet);
    }

    @PostMapping("/company-wallet")
    public Wallet updateCompanyWallet(@RequestParam String id, @RequestParam double companyWallet){
        return walletService.updateCompanyWallet(id,companyWallet);
    }

    @PostMapping("/in-game-wallet")
    public Wallet updateInGameWallet(@RequestParam String id, @RequestParam double inGameWallet){
        return walletService.updateInGameWallet(id,inGameWallet);
    }

    @PostMapping("/withdraw-wallet")
    public Wallet updateWithDrawWallet(@RequestParam String id, @RequestParam double withDrawWallet){
        return walletService.updateWithDrawWallet(id,withDrawWallet);
    }

    @GetMapping("/get-wallet/{id}")
    public Optional<Wallet> getWalletDetails(@PathVariable String id){
        return walletService.getWalletDetails(id);
    }
}
