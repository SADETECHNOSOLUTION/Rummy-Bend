package com.sadetech.rummy_validator.dto;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {

    private String playerId;
    private String name;
    private String phoneNumber;
    private String email;
    private String location;
    private String password;
    private Set<String> role = new HashSet<>();
    private double chips;
    private double inGameWallet;
    private double winningWallet;
    private double missionWallet;
    private double dailyMissionWallet;
    private boolean isWithDraw;
    private boolean isWalletRecharge;
    private String dateOfBirth;
    private String gender;
    private String language;
    private String imagePath;
    private String referrerId;
    private boolean isReferral;
    private int loyaltyPoint;
    private double cashGameWallet; // For each money game you play, the money will be added here from the deposit balance just to calculate the loyalty point
    private double totalDepositMoney;
    private double totalWithdrawMoney;

}
