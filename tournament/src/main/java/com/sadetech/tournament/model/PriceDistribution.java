package com.sadetech.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "price_distribution")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceDistribution {

    @Id
    private String id;
    private String position;
    private double prizeAmount;
    private String ticket;
}
