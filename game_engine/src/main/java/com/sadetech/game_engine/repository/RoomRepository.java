package com.sadetech.game_engine.repository;

import com.sadetech.game_engine.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findByRoomId(String roomId);
    boolean existsByRoomId(String roomId);
}