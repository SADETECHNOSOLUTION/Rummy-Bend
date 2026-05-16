package com.sadetech.user_info.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sadetech.user_info.model.User;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private String playerId;
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String otp;
    private String otpType;
    private String emailOtpContent;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private Set<String> role ;
    private String email;
    private String phoneNumber;
    private String password;
    private double chips;
    private double inGameWallet;
    private double winningWallet;
    private User ourUsers;
    private List<User> ourUsersList;
    private String dateOfBirth;
    private String gender;
    private String imagePath;
    private String referrerId;
    private boolean isReferral;

}


