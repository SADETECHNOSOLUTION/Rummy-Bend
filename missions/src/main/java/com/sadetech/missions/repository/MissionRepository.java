package com.sadetech.missions.repository;

import com.sadetech.missions.model.Mission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@EnableMongoRepositories
public interface MissionRepository extends MongoRepository<Mission,String> {

    List<Mission> findByPlayerId(String playerId);

    List<Mission> findByPlayerIdIsNull();

    List<Mission> findByType(String type);
}
