package com.sadetech.friend_request.controller;

import com.sadetech.friend_request.dto.FriendRequestResponse;
import com.sadetech.friend_request.model.FriendRequest;
import com.sadetech.friend_request.model.Status;
import com.sadetech.friend_request.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/request")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;

    @PostMapping("/send-request")
    public ResponseEntity<String> sendFriendRequest(@RequestBody FriendRequest friendRequest, @RequestHeader("x-player-id")String extractedId){
        String response = friendRequestService.sendFriendRequest(friendRequest,extractedId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Friend request sent. ID: " + response);
    }

    @PutMapping("/update-status")
    public ResponseEntity<String> updateStatus(@RequestParam String id, @RequestParam String status, @RequestHeader("x-player-id")String extractedId){
        Status enumStatus = Status.valueOf(status.toUpperCase());
        String response = friendRequestService.updateRequestStatus(id, enumStatus, extractedId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/request-list")
    public ResponseEntity<List<FriendRequestResponse>> getFriendList(@RequestParam String acceptorId){
        List<FriendRequestResponse> requests = friendRequestService.getFriendRequestsForAcceptor(acceptorId);
            return ResponseEntity.status(HttpStatus.OK).body(requests);
    }

    @DeleteMapping("/remove-friend")
    public ResponseEntity<String> removeFriendFromFriendList(@RequestParam String id, @RequestHeader("x-player-id")String extractedId){
        String response = friendRequestService.removeFriendFromList(id, extractedId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/get-friend-list")
    public ResponseEntity<List<FriendRequestResponse>> getFriendListByStatus(@RequestParam String playerId, @RequestParam String status){
        Status enumStatus = Status.valueOf(status.toUpperCase());
        List<FriendRequestResponse> list = friendRequestService.getFriendRequest(playerId,enumStatus);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
