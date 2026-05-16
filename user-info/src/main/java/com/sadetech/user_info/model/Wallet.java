package com.sadetech.user_info.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "money_transfer")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {

    @Id
    private String id;
    private String playerId;
    private String transactionType;    // Withdraw or Deposit or Send money or Receive money
    private String process; // From game winning, deduct for room entry, get from refer and earn, get from rewards, get from mission
    private LocalDateTime transactionMadeAt;
    private String creditType; // Credited, Debited
    private double transactionAmount;
    private String referenceId;
}
