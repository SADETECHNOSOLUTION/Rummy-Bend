package com.sadetech.point_allocation.dto;

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
}
