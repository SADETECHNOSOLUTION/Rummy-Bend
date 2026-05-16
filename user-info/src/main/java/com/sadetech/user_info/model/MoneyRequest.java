package com.sadetech.user_info.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "money_request")
public class MoneyRequest {

    @Id
    private String id;
    private String requestPlayerId;
    private String senderPlayerId;
    private double amount;
    private String requestSummaryStatus; // Request received, Failed, Success
    private String requestType; // Send or Receive
    @CreatedDate
    private LocalDateTime requestedTime;
}
