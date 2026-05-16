package com.sadetech.user_info.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardDto {
    private String id;
    private String heading;
    private String stage;
    private String task;
    private String coupon;
    @CreatedDate
    private LocalDateTime createdAt;
    private double depositMoney;  // Minimum deposit money
    private double bonusLimit;     // Get bonus up to ****
    private double bonusPercentage;
    private String remarks;
    private String progress = "Redeem now";
    private String playerId;
}
