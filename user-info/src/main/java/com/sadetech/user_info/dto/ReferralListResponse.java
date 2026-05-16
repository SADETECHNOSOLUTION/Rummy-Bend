package com.sadetech.user_info.dto;

import lombok.Data;

@Data
public class ReferralListResponse {

    private String playerId;
    private String name;
    private int referralRank;
    private String referralStatus;

}
