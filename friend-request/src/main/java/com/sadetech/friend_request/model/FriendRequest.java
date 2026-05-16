package com.sadetech.friend_request.model;

import com.fasterxml.jackson.databind.annotation.EnumNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "friend_request")
public class FriendRequest {

    @Id
    private String id;
    private String requesterId;
    private String acceptorId;
    private Status status;
    @CreatedDate
    private LocalDateTime requestSentTime;
    private LocalDateTime acceptedOrDeclinedTime;
}
