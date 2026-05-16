package com.sadetech.missions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "rewards")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rewards {

    @Id
    private String id;
    private String heading;
    private String stage;  // Rewards
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
