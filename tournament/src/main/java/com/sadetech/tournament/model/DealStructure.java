package com.sadetech.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "deal_structure")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealStructure {

    @Id
    private String id;
    private int round;
    private int room;
    private int players;
    private int promoted;
}
