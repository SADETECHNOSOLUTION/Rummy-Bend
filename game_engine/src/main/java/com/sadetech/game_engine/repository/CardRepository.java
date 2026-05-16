package com.sadetech.game_engine.repository;

import com.sadetech.game_engine.model.Card;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends MongoRepository<Card, String> {
    Card findByRoomId(String roomId);
}

