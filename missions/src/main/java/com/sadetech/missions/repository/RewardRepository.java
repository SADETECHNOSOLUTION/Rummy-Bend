package com.sadetech.missions.repository;

import com.sadetech.missions.model.Rewards;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableMongoRepositories
public interface RewardRepository extends MongoRepository<Rewards,String> {
    List<Rewards> findByPlayerId(String playerId);

    List<Rewards> findByPlayerIdIsNull();

    List<Rewards> findRewardsByPlayerId(String playerId, double totalDepositMoney);

    List<Rewards> findByStageAndProgress(String stage, String redeemed);
}
