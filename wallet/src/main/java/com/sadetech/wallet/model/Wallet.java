package com.sadetech.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "wallet")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    private String id;
    private double companyWallet;  // Charges for every match, for company
    private double inGameWallet; // Managing in game wallet money, which can't be withdrawn by user, only can add money to this wallet
    private double withDrawWallet; // Winning amount of the players money, can be withdrawn by user.
}

