package com.sadetech.game_engine.repository;

import com.sadetech.game_engine.model.Points;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends MongoRepository<Points,String> {

    Optional<Points> findByPlayerCountAndTypeAndDefaultValue(int playerCount, String type, int defaultValue);
}
