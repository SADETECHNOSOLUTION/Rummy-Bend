package com.sadetech.user_info.dto;

import lombok.Data;

@Data
public class RequestDto {

    private String playerId;
    private String name;
    private String phoneNumber;
    private String email;
    private double chips;
    private double inGameWallet;
    private double winningWallet;
}
