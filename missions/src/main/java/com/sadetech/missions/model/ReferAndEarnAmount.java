package com.sadetech.missions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "refer_and_earn_reward_amount")
public class ReferAndEarnAmount {

    @Id
    private String id;
    private int rank;
    private double amount;

}
