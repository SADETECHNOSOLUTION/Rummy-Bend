package com.sadetech.room_creation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {

    @Id
    private String id;
    private String playerId;
    private String transactionType;    // Withdraw or Deposit
    private String process; // From game winning, deduct for room entry, get from refer and earn, get from rewards, get from mission
    private LocalDateTime transactionMadeAt;
    private String creditType; // Credited, Debited
    private double transactionAmount;
    private String referenceId;
}
