package com.sadetech.room_creation.repository;

import com.sadetech.room_creation.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableMongoRepositories
public interface RoomRepository extends MongoRepository<Room,String> {

    List<Room> findByTournamentId(String tournamentId);

    List<Room> findByGameStatus(String gameStatus);

    List<Room> findByRoomSizeAndRoomTypeAndGameModeAndGameStatusAndPointValue(int roomSize, String roomType, String gameMode, String gameStatus, double pointValue);

    Page<Room> findAll(Pageable pageable);

    @Query("{ 'playerDetails.playerId': ?0, 'roomSize': ?1, 'roomType': ?2, 'issuedPoint': ?3 }")
    List<Room> findRoomsByPlayerIdAndAttributes(String playerId, int roomSize, String roomType, int issuedPoint);

}
