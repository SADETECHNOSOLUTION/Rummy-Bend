package com.sadetech.game_engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "point_setting")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Points {

    @Id
    private String id;
    private String type; // Point , Pool , Deal
    private int playerCount;  // 2 , 6 , 9
    private int defaultValue; //0 , 80 , 160 , 240
    private List<PointValue> pointValue;
}
