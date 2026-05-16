package com.sadetech.point_allocation.controller;

import com.sadetech.point_allocation.feign.RummyValidationFeignClient;
import com.sadetech.point_allocation.model.Point;
import com.sadetech.point_allocation.model.RejoinRequest;
import com.sadetech.point_allocation.service.PointService;
import com.sadetech.point_allocation.service.RejoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/point")
public class    PointController {

    @Autowired
    private PointService pointService;

    @Autowired
    private RejoinService rejoinService;

    @Autowired
    private RummyValidationFeignClient rummyValidationFeignClient;

    @PostMapping("/save")
    public ResponseEntity<Point> savePoint(
            @RequestParam String roomId,
            @RequestParam String declaredPlayerId,
            @RequestParam(required = false) Integer exceedPoint) {

        // Fetch validation result using Feign Client
        Map<String, Object> validationResult = rummyValidationFeignClient.getValidationResult(roomId);

        if (validationResult == null || validationResult.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Call service to save the data
        Point savedPoint = pointService.savePointData(validationResult, declaredPlayerId, exceedPoint);

        return ResponseEntity.ok(savedPoint);
    }

    @PostMapping("/update")
    public Point updatePoint(@RequestBody Map<String, Object> validationResult,
                             @RequestParam int round,
                             @RequestParam String declaredPlayerId) {
        return pointService.updatePointData(validationResult, round, declaredPlayerId);
    }

    @GetMapping("/result/{roomId}")
    public ResponseEntity<?> getResult(@PathVariable String roomId, @RequestParam String gameStatus){
            List<Point> point = pointService.getByRoomId(roomId, gameStatus);
            return ResponseEntity.ok(point);
    }

    @GetMapping("/get-all-result/{roomId}")
    public ResponseEntity<?> getAllRoundResult(@PathVariable String roomId){
        List<Point> points = pointService.getResultByRoomId(roomId);
        return ResponseEntity.ok(points);

    }

    @PostMapping("/rejoin-decision")
    public ResponseEntity<String> handleRejoinDecision(@RequestBody RejoinRequest request) {
        boolean decision = request.isWantsToRejoin();
        rejoinService.updateRejoinDecision(request.getPlayerId(), request.getRoomId(), decision);
        return ResponseEntity.ok("Decision received successfully");
    }

    @GetMapping("/rejoin-decision/{playerId}/{roomId}")
    boolean checkRejoinDecision(@PathVariable("playerId") String playerId, @PathVariable("roomId") String roomId) {
      return rejoinService.getRejoinDecision(playerId,roomId);
    }
}
