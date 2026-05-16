package com.sadetech.user_info.service;

import com.sadetech.user_info.exception.ResourceNotFoundException;
import com.sadetech.user_info.model.Wallet;
import com.sadetech.user_info.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public String settleAmount(Wallet wallet){
        walletRepository.save(wallet);
        return "Amount settled successfully";
    }

    public List<Wallet> getWalletDetails(String playerId){

        if(playerId == null || playerId.isBlank()){
            throw new IllegalArgumentException("Player id should not be empty");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if ( !playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        List<Wallet> getDetails = walletRepository.findByPlayerId(playerId);
        if(getDetails.isEmpty()){
            throw new ResourceNotFoundException("No transaction details found");
        }

        return getDetails;
    }


}
