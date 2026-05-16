package com.sadetech.tournament.controller;

import com.sadetech.tournament.model.Tournament;
import com.sadetech.tournament.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournament")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    private static final Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @PostMapping("/create")
    public Tournament createTournament(@RequestBody Tournament tournament){
       logger.info("Tournament Details : {} ", tournament);
        return tournamentService.createTournament(tournament);
    }

    @PostMapping("/createRooms")
    public Tournament createRoomsForPlayers( @RequestParam int defaultPoint) {
        return tournamentService.createRoomForPlayers(defaultPoint);
    }

    @GetMapping("/get/{id}")
    public Tournament getTournament(@PathVariable String id){
        return tournamentService.getTournamentDetails(id);
    }

    @PatchMapping("/join/{id}")
    public ResponseEntity<?> addPlayers(@PathVariable String id,
                                        @RequestParam String playerId,
                                        @RequestHeader("x-player-id") String extractedId){

            Tournament tournament = tournamentService.updatePlayers(id, playerId,extractedId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(tournament);
    }

    @GetMapping("/{tournamentId}/score-board")
    public ResponseEntity<List<Map.Entry<String, Integer>>> getRemainingScoresByRoom(
            @PathVariable String tournamentId) {
        try {
            List<Map.Entry<String, Integer>> scores = tournamentService.getRemainingScoresByRoom(tournamentId);
            return ResponseEntity.ok(scores);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Return 400 for invalid tournament ID
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Return 500 for other errors
        }
    }

    @GetMapping("/{id}/select-players")
    public ResponseEntity<Map<String, Object>> selectPlayerForNextRound(@PathVariable String id, @RequestParam int minimumQualifyPoint) {
        Tournament tournament = tournamentService.selectPlayerForNextRound(id,minimumQualifyPoint);

        // Prepare the response object
        Map<String, Object> response = new HashMap<>();
        response.put("selectedPlayers", tournament.getSelectedPlayers()); // Assuming selectedPlayers is saved in tournament
        response.put("tournament", tournament);

        return ResponseEntity.ok(response); // Return both selected players and tournament details
    }

    @GetMapping("/get-all-tournament")
    public ResponseEntity<List<Tournament>> getAllTournament(){
        List<Tournament> tournaments = tournamentService.getAllTournament();
        return ResponseEntity.status(HttpStatus.OK).body(tournaments);
    }

    @GetMapping("/get-playerlist/{tournamentId}")
    public ResponseEntity<List<String>> playerList (@PathVariable String tournamentId){
        List<String> list = tournamentService.getPlayerList(tournamentId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @GetMapping("/selected-playerlist/{tournamentId}")
    public ResponseEntity<List<String>> selectedPlayerList (@PathVariable String tournamentId){
        List<String> list = tournamentService.getSelectedPlayerList(tournamentId);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @DeleteMapping("/delete-tournament/{id}")
    public ResponseEntity<String> deleteTournament(@PathVariable String id) {
        String response = tournamentService.deleteTournamentById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-status")
    public ResponseEntity<String> updateTournament(@RequestParam String id, @RequestParam String tournamentStatus){
        String response = tournamentService.updateTournamentStatus(id, tournamentStatus);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/update-time")
    public ResponseEntity<String> updateTournamentTime(@RequestParam String id, @RequestParam LocalTime matchStartingAt){
        String response = tournamentService.updateTournamentStartingTime(id, matchStartingAt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/last-7-days")
    public ResponseEntity<List<Tournament>> getTournamentDetails(){
        List<Tournament> tournaments = tournamentService.getLast7DaysTournament();
        return ResponseEntity.status(HttpStatus.OK).body(tournaments);
    }
}