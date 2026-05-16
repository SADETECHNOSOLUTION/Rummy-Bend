package com.sadetech.user_info.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String playerId;
    private String message;
    private String token;
    private String refreshToken;
    private int statusCode;

}
