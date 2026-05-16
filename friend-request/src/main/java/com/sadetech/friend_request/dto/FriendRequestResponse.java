package com.sadetech.friend_request.dto;

import com.sadetech.friend_request.model.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FriendRequestResponse {

    private String id;
    private String requesterId;
    private String requesterName;
    private String acceptorId;
    private String acceptorName;
    private Status status;
    private LocalDateTime requestSentTime;
    private LocalDateTime acceptedOrDeclinedTime;

}
