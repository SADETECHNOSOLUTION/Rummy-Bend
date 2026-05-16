package com.sadetech.friend_request.service;

import com.sadetech.friend_request.configuration.ModelMapperConfig;
import com.sadetech.friend_request.dto.FriendRequestResponse;
import com.sadetech.friend_request.exception.RequestNotFoundException;
import com.sadetech.friend_request.exception.UnAuthorizedAccessException;
import com.sadetech.friend_request.exception.UserNotFoundException;
import com.sadetech.friend_request.feign.UserFeignClient;
import com.sadetech.friend_request.feignDto.UserDto;
import com.sadetech.friend_request.model.FriendRequest;
import com.sadetech.friend_request.model.Status;
import com.sadetech.friend_request.repository.FriendRequestRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FriendRequestService {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ModelMapper modelMapper;

    public String sendFriendRequest(FriendRequest friendRequest, String extractedId) {

        String requesterId = friendRequest.getRequesterId();
        String acceptorId = friendRequest.getAcceptorId();

        if(requesterId == null || requesterId.isBlank()){
            throw new IllegalArgumentException("Requester id should not be empty.");
        }

        requesterId = requesterId.trim().replaceAll("\\s+", "");

        if ( !requesterId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Requester ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(requesterId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if(acceptorId == null || acceptorId.isBlank()){
            throw new IllegalArgumentException("Acceptor id should not be empty.");
        }

        acceptorId = acceptorId.trim().replaceAll("\\s+", "");

        if ( !acceptorId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Acceptor ID must be a valid 24-character hexadecimal string.");
        }

        userFeignClient.getDetails(requesterId).orElseThrow(() -> new UserNotFoundException("No user found for the request player id"));
        userFeignClient.getDetails(acceptorId).orElseThrow(() -> new UserNotFoundException("User not found for the acceptor Id"));

        // Prevent sending request to self
        if (requesterId.equals(acceptorId)) {
            throw new IllegalArgumentException("Cannot send a friend request to yourself.");
        }

        // Check if a request or friendship exists in either direction
        Optional<FriendRequest> existing = friendRequestRepository.findExistingFriendship(requesterId, acceptorId);

        if (existing.isPresent()) {
            FriendRequest existingRequest = existing.get();
            Status status = existingRequest.getStatus();
            if (status == Status.PENDING) {
                throw new IllegalStateException("A friend request already exists and is pending.");
            } else if (status == Status.ACCEPTED) {
                throw new IllegalStateException("You are already friends.");
            }else if (status == Status.REJECTED){
                existingRequest.setStatus(Status.PENDING);
                existingRequest.setRequestSentTime(java.time.LocalDateTime.now()); // optional: update time
                return friendRequestRepository.save(existingRequest).getId();
            }
        }

        friendRequest.setStatus(Status.PENDING);
        return friendRequestRepository.save(friendRequest).getId(); // You can also return the ID if you want
    }

    public String updateRequestStatus(String id, Status status, String extractedId) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Request ID should not be empty.");
        }

        if (status == null) {
            throw new IllegalArgumentException("Status must not be null.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if (!id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Request ID must be a valid 24-character hexadecimal string.");
        }

        FriendRequest friendRequest = friendRequestRepository
                .findById(id)
                .orElseThrow(() -> new RequestNotFoundException("No request found for the given request ID."));

        if (!extractedId.equals(friendRequest.getAcceptorId())){
            throw new UnAuthorizedAccessException("Access denied");
        }

        Status existingStatus = friendRequest.getStatus();

        if (existingStatus == Status.REJECTED) {
            throw new IllegalArgumentException("It was already rejected.");
        }

        if (existingStatus == Status.ACCEPTED) {
            throw new IllegalArgumentException("You are already friends.");
        }

        if (existingStatus.equals(status)) {
            throw new IllegalArgumentException("Trying to push the same status.");
        }

        friendRequest.setStatus(status);
        friendRequest.setAcceptedOrDeclinedTime(LocalDateTime.now());

        friendRequestRepository.save(friendRequest);

        return "Friend request has been " + status.name().toLowerCase() + ".";
    }

    public List<FriendRequestResponse> getFriendRequestsForAcceptor(String acceptorId) {

        if (acceptorId == null || acceptorId.isBlank()) {
            throw new IllegalArgumentException("Input should not be empty.");
        }

        acceptorId = acceptorId.trim().replaceAll("\\s+", "");

        if (!acceptorId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Request ID must be a valid 24-character hexadecimal string.");
        }

        List<FriendRequest> requests = friendRequestRepository.findAllByAcceptorId(acceptorId);

        if (requests.isEmpty()) {
            throw new RequestNotFoundException("No friend requests found for the acceptor ID.");
        }

        List<FriendRequestResponse> responseList = new ArrayList<>();


        for (FriendRequest request : requests) {
            Optional<UserDto> requesterDtoOptional = userFeignClient.getDetails(request.getRequesterId());
            Optional<UserDto> acceptorDtoOptional = userFeignClient.getDetails(request.getAcceptorId());

            if (requesterDtoOptional.isEmpty() || acceptorDtoOptional.isEmpty()) {
                throw new IllegalArgumentException("Requester or Acceptor not found");
            }

            // Map fields from FriendRequest
            FriendRequestResponse response = modelMapper.map(request, FriendRequestResponse.class);

            // Set additional fields
            response.setRequesterName(requesterDtoOptional.get().getName());
            response.setAcceptorName(acceptorDtoOptional.get().getName());

            responseList.add(response);
        }

        return responseList;
    }


    public String removeFriendFromList(String id, String extractedId) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Request ID should not be empty.");
        }

        id = id.trim().replaceAll("\\s+", "");

        if (!id.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Request ID must be a valid 24-character hexadecimal string.");
        }

        FriendRequest friendRequest = friendRequestRepository
                .findById(id)
                .orElseThrow(() -> new RequestNotFoundException("No request found for the given request ID."));

        if(!extractedId.equals(friendRequest.getRequesterId()) || !extractedId.equals(friendRequest.getAcceptorId())){
            throw new UnAuthorizedAccessException("Access denied");
        }

        Status status = friendRequest.getStatus();

        if (status == Status.PENDING) {
            throw new IllegalArgumentException("Friend request is still pending. Cannot remove.");
        } else if (status == Status.REJECTED) {
            throw new IllegalArgumentException("Friend request was already rejected. You are not friends.");
        } else if (status != Status.ACCEPTED) {
            throw new IllegalArgumentException("Invalid friend status. Cannot remove.");
        }

        friendRequestRepository.deleteById(friendRequest.getId());
        return "User has been removed from the friend list successfully.";
    }

    public List<FriendRequestResponse> getFriendRequest(String playerId, Status status){

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        List<FriendRequest> getRequest = friendRequestRepository.findByPlayerIdAndStatus(playerId,status);
        if(getRequest.isEmpty()){
            throw new RequestNotFoundException("No request found");
        }

        List<FriendRequestResponse> requestResponses = new ArrayList<>();

        for (FriendRequest request : getRequest){

            UserDto existingAcceptorUser = userFeignClient.getDetails(request.getAcceptorId()).orElseThrow();
            UserDto existingRequesterUser = userFeignClient.getDetails(request.getRequesterId()).orElseThrow();

            FriendRequestResponse response = modelMapper.map(request, FriendRequestResponse.class);
            response.setRequesterName(existingRequesterUser.getName());
            response.setAcceptorName(existingAcceptorUser.getName());

            requestResponses.add(response);
        }

        return requestResponses;

    }

}
