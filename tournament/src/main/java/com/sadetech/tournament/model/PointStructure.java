package com.sadetech.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "point_structure")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointStructure {

    @Id
    private String id;
    private int round;
    private String duration;
    private int pointValue;
    private int minimumChips;
    private String rebuyFee;
    private int finalChipsCount;

}
