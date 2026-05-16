package com.sadetech.user_info.repository;

import com.sadetech.user_info.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends MongoRepository<Wallet,String> {
    List<Wallet> findByPlayerId(String playerId);
}
