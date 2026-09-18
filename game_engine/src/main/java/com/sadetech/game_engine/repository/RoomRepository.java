package com.sadetech.game_engine.repository;

import com.sadetech.game_engine.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {
    Optional<Room> findByRoomId(String roomId);
    boolean existsByRoomId(String roomId);

    // Standard query matching String ID directly to MongoDB's ObjectId
    @Query("{ '_id': { $oid: ?0 } }")
    Optional<Room> findRoomById(String roomId);

}