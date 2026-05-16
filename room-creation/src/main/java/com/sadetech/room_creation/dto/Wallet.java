package com.sadetech.room_creation.dto;

import lombok.Data;

@Data
public class Wallet {

    private String id;
    private double companyWallet;  // Charges for every match, for company
    private double inGameWallet; // Managing in game wallet money, which can't be withdrawn by user, only can add money to this wallet
    private double withDrawWallet; // Winning amount of the players money, can be withdrawn by user.
}
