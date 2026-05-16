package com.sadetech.point_allocation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "rejoin-request")
public class RejoinRequest {
    private String playerId;
    private String roomId;
    private boolean wantsToRejoin;
}
