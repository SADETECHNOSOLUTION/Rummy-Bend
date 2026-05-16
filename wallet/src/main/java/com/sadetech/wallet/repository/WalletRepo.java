package com.sadetech.wallet.repository;

import com.sadetech.wallet.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepo extends MongoRepository<Wallet,String> {
}
