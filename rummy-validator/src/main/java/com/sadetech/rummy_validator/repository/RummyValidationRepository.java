package com.sadetech.rummy_validator.repository;

import com.sadetech.rummy_validator.model.RummyValidation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RummyValidationRepository extends MongoRepository<RummyValidation, String> {
}
