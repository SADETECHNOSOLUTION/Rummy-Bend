package com.sadetech.rummy_validator.controller;

import com.sadetech.rummy_validator.model.RummyValidation;
import com.sadetech.rummy_validator.model.RummyValidationResponse;
import com.sadetech.rummy_validator.repository.RummyValidationResponseRepository;
import com.sadetech.rummy_validator.service.CardValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rummy")
public class ValidationController {

    @Autowired
    private CardValidationService cardValidationService;

    @Autowired
    private RummyValidationResponseRepository responseRepository;

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePlayersCards(@RequestBody RummyValidation request) {
        Map<String, Object> result = cardValidationService.validateAllPlayersCards(
                request.getRoomId(),
                request.getPlayersCards(),
                request.getPlayerTotalPoints(),
                request.getPlayerDropType()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get/{roomId}")
    public ResponseEntity<?> getValidationByRoomId(@PathVariable String roomId) {
        Optional<RummyValidationResponse> response = responseRepository.findByRoomId(roomId);
        return response.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Validation not found"));
    }

    @PostMapping("/validate-card")
    public Map<String, Map<String, Object>> validateCards(@RequestBody Map<String, List<List<Map<String, String>>>> playerCards, @RequestParam String roomId) {
        return cardValidationService.validateCards(playerCards, roomId);
    }


}
