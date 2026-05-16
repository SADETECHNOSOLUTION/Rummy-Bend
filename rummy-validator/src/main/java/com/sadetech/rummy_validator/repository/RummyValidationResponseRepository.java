package com.sadetech.rummy_validator.repository;

import com.sadetech.rummy_validator.model.RummyValidationResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RummyValidationResponseRepository extends MongoRepository<RummyValidationResponse, String> {
    Optional<RummyValidationResponse> findByRoomId(String roomId);
}
