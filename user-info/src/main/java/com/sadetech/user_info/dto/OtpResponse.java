package com.sadetech.user_info.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OtpResponse {
    private String id;
    private String phoneNumber;
    private String otp;
    private String otpType;
    private LocalDateTime createdAt;
}
