package com.sadetech.missions.repository;

import com.sadetech.missions.model.ReferAndEarnAmount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferAndEarnRepository extends MongoRepository<ReferAndEarnAmount,String> {
    Optional<ReferAndEarnAmount> findByRank(int rank);
}
