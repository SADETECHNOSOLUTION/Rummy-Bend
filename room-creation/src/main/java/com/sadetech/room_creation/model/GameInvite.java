package com.sadetech.room_creation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "game_invite")
public class GameInvite {

    @Id
    private String id;
    private String roomId;
    private String roomOwnerId;
    private List<ParticipantStatus> participantStatusList;
    @CreatedDate
    private LocalDateTime inviteSentAt;
}
