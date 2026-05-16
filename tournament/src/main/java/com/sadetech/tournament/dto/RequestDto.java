package com.sadetech.tournament.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private String playerId;
    private String name;
    private String phoneNumber;
    private String email;
    private double chips;
    private double inGameWallet;
    private double winningWallet;
}