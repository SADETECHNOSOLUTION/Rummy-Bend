package com.sadetech.settings.repository;

import com.sadetech.settings.model.Settings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsRepository extends MongoRepository<Settings,String > {
    Optional<Settings> findByPlayerId(String playerId);
}
