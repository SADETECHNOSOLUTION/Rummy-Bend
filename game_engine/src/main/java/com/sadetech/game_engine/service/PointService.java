package com.sadetech.game_engine.service;

import com.sadetech.game_engine.exception.PointsNotFoundException;
import com.sadetech.game_engine.model.PointValue;
import com.sadetech.game_engine.model.Points;
import com.sadetech.game_engine.repository.PointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    public Points addDetails(Points points){
        return pointRepository.save(points);
    }

    public Points getPoints(int playerCount, String type, int defaultValue){

        return pointRepository.findByPlayerCountAndTypeAndDefaultValue(playerCount,type,defaultValue)
                .orElseThrow(() -> new PointsNotFoundException("No value found"));
    }

    public Points updatePointValue(int playerCount, String type, int defaultValue, int id, double newPointValue, double newMoney) {
        // Fetch the existing Points document
        Optional<Points> optionalPoints = pointRepository.findByPlayerCountAndTypeAndDefaultValue(playerCount, type, defaultValue);

        Points points;
        if (optionalPoints.isPresent()) {
            points = optionalPoints.get();

            // Find the PointValue with the given ID
            List<PointValue> pointValues = points.getPointValue();
            boolean updated = false;

            for (PointValue pv : pointValues) {
                if (pv.getId() == id) {
                    // Update existing point value and money
                    pv.setPointValue(newPointValue);
                    pv.setMoney(newMoney);
                    updated = true;
                    break;
                }
            }

            // If no matching PointValue ID was found, add a new entry
            if (!updated) {
                PointValue newPoint = new PointValue();
                newPoint.setId(id);
                newPoint.setPointValue(newPointValue);
                newPoint.setMoney(newMoney);
                pointValues.add(newPoint);
            }
        } else {
            // If Points document doesn't exist, create a new one
            points = new Points();
            points.setType(type);
            points.setPlayerCount(playerCount);
            points.setDefaultValue(defaultValue);

            // Create new PointValue list and add the new entry
            List<PointValue> pointValues = new ArrayList<>();
            PointValue newPoint = new PointValue();
            newPoint.setId(id);
            newPoint.setPointValue(newPointValue);
            newPoint.setMoney(newMoney);
            pointValues.add(newPoint);

            points.setPointValue(pointValues);
        }

        // Save the updated/new Points document
        return pointRepository.save(points);
    }

    public Points deletePointValue(int playerCount, String type, int defaultValue, int id) {
        Optional<Points> optionalPoints = pointRepository.findByPlayerCountAndTypeAndDefaultValue(playerCount, type, defaultValue);

        if (optionalPoints.isPresent()) {
            Points points = optionalPoints.get();
            List<PointValue> pointValues = points.getPointValue();

            // Remove the PointValue with the given ID
            pointValues.removeIf(pv -> pv.getId() == id);

            // If all PointValues are removed, delete the entire Points document
            if (pointValues.isEmpty()) {
                pointRepository.delete(points);
                return null; // Returning null indicates the document was deleted
            }

            // Save and return the updated Points document
            return pointRepository.save(points);
        } else {
            throw new PointsNotFoundException("No points found for given criteria");
        }
    }

}
