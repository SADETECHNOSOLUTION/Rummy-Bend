package com.sadetech.room_creation.dto;

import com.sadetech.room_creation.model.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteResponse {

    private String id;
    private String roomId;
    private String roomOwnerId;
    private String roomOwnerName;
    private List<ParticipantStatus> participantStatusList;
    private LocalDateTime inviteSentAt;
    private int roomSize;
    private String roomType; //Pool,Deal,Point
    private String gameMode; //Practice,Cash
    private int issuedPoint;
    private String  entryType; //Chips or Money
    private double entryPrice; //Chips or Money deduced for a single player

}
