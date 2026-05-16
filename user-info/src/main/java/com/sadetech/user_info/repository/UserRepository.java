package com.sadetech.user_info.repository;

import com.sadetech.user_info.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByReferrerId(String referrerId);

    @Query("{ $or: [ { 'email' : ?0 }, { 'phoneNumber' : ?0 } ] }")
    Optional<User> findByEmailOrPhoneNumber(String emailOrPhone);

    @Query("{ $or: [ { 'playerId' : ?0 }, { 'email' : ?0 }, { 'phoneNumber' : ?0 }]}")
    Optional<User> findByPlayerIdOrEmailOrPhone(String idOrEmailOrPhone);

    int countByReferrerId(String referrerId);
}