package com.sadetech.room_creation.dto;

import lombok.Data;

@Data
public class RequestDTO {

    private String playerId;
    private String name;
    private String phoneNumber;
    private String email;
    private double chips;
    private double inGameWallet;
    private double winningWallet;
    private double missionWallet;
    private boolean isWithDraw;
    private boolean isWalletRecharge;
    private int loyaltyPoint;
    private double cashGameWallet;
}
