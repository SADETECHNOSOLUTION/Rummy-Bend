package com.sadetech.room_creation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantStatus {

    private String participantId;
    private Status inviteStatus;

}
