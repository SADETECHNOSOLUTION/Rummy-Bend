package com.sadetech.game_engine.repository;

import com.sadetech.game_engine.model.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends MongoRepository<Card, String> {
    Card findByRoomId(String roomId);
    Optional<Card> findFirstByRoomId(String roomId);
}

