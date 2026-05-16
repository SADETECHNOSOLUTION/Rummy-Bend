package com.sadetech.game_engine.repository;

import com.sadetech.game_engine.model.CardUpdateLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardUpdateLogRepository extends MongoRepository<CardUpdateLog, String> {
    List<CardUpdateLog> findByRoomId(String roomId);
}
