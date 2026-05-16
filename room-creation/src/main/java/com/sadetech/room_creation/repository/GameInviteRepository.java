package com.sadetech.room_creation.repository;

import com.sadetech.room_creation.model.GameInvite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameInviteRepository extends MongoRepository<GameInvite,String> {
    List<GameInvite> findByParticipantStatusList_ParticipantId(String playerId);

    Optional<GameInvite> findByRoomId(String roomId);
}
