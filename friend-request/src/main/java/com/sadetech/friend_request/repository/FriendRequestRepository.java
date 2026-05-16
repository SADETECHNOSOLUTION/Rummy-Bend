package com.sadetech.friend_request.repository;

import com.sadetech.friend_request.model.FriendRequest;
import com.sadetech.friend_request.model.Status;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends MongoRepository<FriendRequest,String> {
    Optional<FriendRequest> findByRequesterIdAndAcceptorId(String requesterId, String acceptorId);

    @Query("{$or: [ { 'requesterId': ?0, 'acceptorId': ?1 }, { 'requesterId': ?1, 'acceptorId': ?0 } ] }")
    Optional<FriendRequest> findExistingFriendship(String requesterId, String acceptorId);

    List<FriendRequest> findAllByAcceptorId(String acceptorId);

    @Query("{ $and: [ { $or: [ { 'requesterId': ?0 }, { 'acceptorId': ?0 } ] }, { 'status': ?1 } ] }")
    List<FriendRequest> findByPlayerIdAndStatus(String playerId, Status status);

}
