package com.sadetech.room_creation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDetails {

    private String playerId;
    private int issuedPoint;
}
