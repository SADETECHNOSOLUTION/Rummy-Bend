package com.sadetech.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "late_join")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LateJoin {

    @Id
    private String id;
    private int round;
    private String lateJoinFee;
    private int lateJoinChips;
}
