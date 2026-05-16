package com.sadetech.kyc_verification.repository;

import com.sadetech.kyc_verification.model.Kyc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycRepository extends MongoRepository<Kyc,String> {
    Kyc findByPlayerId(String playerId);
}
