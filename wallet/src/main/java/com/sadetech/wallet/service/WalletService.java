package com.sadetech.wallet.service;

import com.sadetech.wallet.exception.WalletNotFoundException;
import com.sadetech.wallet.model.Wallet;
import com.sadetech.wallet.repository.WalletRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepo walletRepo;

    public Wallet createWallet(Wallet wallet){
        return walletRepo.save(wallet);
    }

    public Wallet updateCompanyWallet(String id, double companyWallet){

        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if ( !id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        Wallet wallet = walletRepo.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("No id found"));

            wallet.setCompanyWallet(companyWallet);

        return walletRepo.save(wallet);
    }

    public Wallet updateInGameWallet(String id, double inGameWallet){

        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if ( !id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }
        
        Wallet wallet = walletRepo.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("No id found"));

        wallet.setInGameWallet(inGameWallet);

        return walletRepo.save(wallet);
    }

    public Wallet updateWithDrawWallet(String id, double withDrawWallet){

        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if ( !id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }
        
        Wallet wallet = walletRepo.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("No id found"));

        wallet.setWithDrawWallet(withDrawWallet);

        return walletRepo.save(wallet);
    }

    public Optional<Wallet> getWalletDetails(String id){

        if(id == null || id.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if ( !id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        return Optional.ofNullable(walletRepo.findById(id)
                .orElseThrow(() -> new WalletNotFoundException("No wallet found for the id")));
    }
}
