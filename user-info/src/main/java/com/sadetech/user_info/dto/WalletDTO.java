package com.sadetech.user_info.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {

    private double companyWallet;  // Charges for every match, for company
    private double inGameWallet; // Managing in game wallet money, which can't be withdrawn by user, only can add money to this wallet
    private double withDrawWallet;

}
