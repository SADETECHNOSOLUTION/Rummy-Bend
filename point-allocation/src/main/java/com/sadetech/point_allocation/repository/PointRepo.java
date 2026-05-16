package com.sadetech.point_allocation.repository;

import com.sadetech.point_allocation.model.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepo extends MongoRepository<Point,String> {


    List<Point> findByRoomIdAndGameStatus(String roomId, String gameStatus);

    Point findByRoomIdAndRound(String roomId, int round);

    List<Point> findByRoomId(String roomId);
}
