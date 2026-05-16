package com.sadetech.room_creation.controller;

import com.sadetech.room_creation.dto.InviteResponse;
import com.sadetech.room_creation.dto.StringResponse;
import com.sadetech.room_creation.feign.UserFeignClient;
import com.sadetech.room_creation.model.GameInvite;
import com.sadetech.room_creation.model.Room;
import com.sadetech.room_creation.model.Status;
import com.sadetech.room_creation.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserFeignClient userFeignClient;

    @PostMapping("/create-room")
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        Room createdRoom = roomService.createRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getRoom(
            @RequestParam int roomSize,
            @RequestParam String roomType,
            @RequestParam String gameMode,
            @RequestParam String gameStatus,
            @RequestParam double pointValue)
    {
        Optional<Room> room = roomService.getRoom(roomSize, roomType, gameMode, gameStatus, pointValue);
        return ResponseEntity.ok(room);
    }

    @PutMapping("/update-9/{roomId}")
    public ResponseEntity<?> updateRoomForNine(
            @PathVariable String roomId,
            @RequestParam(required = false) String gameStatus,
            @RequestParam String playerId,
            @RequestHeader("x-player-id") String extractedId
    ) {
       Room room = roomService.updateRoomForNinePlayer(roomId,gameStatus,playerId,extractedId);
       return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @PutMapping("/update-6/{roomId}")
    public ResponseEntity<?> updateRoomForSix(
            @PathVariable String roomId,
            @RequestParam(required = false) String gameStatus,
            @RequestParam(required = false) String playerId,
            @RequestHeader("x-player-id") String extractedId
    ) {
        Room room = roomService.updateRoomForSixPlayer(roomId,gameStatus, playerId,extractedId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @PutMapping("/update-2/{roomId}")
    public ResponseEntity<?> updateRoom(
            @PathVariable String roomId,
            @RequestParam(required = false) String gameStatus,
            @RequestParam String playerId,
            @RequestHeader("x-player-id") String extractedId
    ) {

            Room room = roomService.updateRoomForTwoPlayer(roomId, gameStatus, playerId, extractedId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @PostMapping("/shuffle-start/{roomId}")
    public ResponseEntity<?> shuffleCardIfStarted(@PathVariable String roomId){

            String response = roomService.shuffleCardIfStarted(roomId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new StringResponse(response));

    }

    @GetMapping("/get-detail/{roomId}")
    public ResponseEntity<Room> getRoomDetails(@PathVariable String roomId){
        Room room = roomService.getDetailsByRoomId(roomId);
        return ResponseEntity.status(HttpStatus.OK).body(room);
    }

    @PostMapping("/update-status/{roomId}")
    public ResponseEntity<?> updateGameStatus(@PathVariable String roomId,@RequestParam String gameStatus){
            Room room = roomService.updateGameStatus(roomId,gameStatus);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @PostMapping("/update-match-winner/{roomId}")
    public ResponseEntity<?> updateGameWinner(@PathVariable String roomId,@RequestParam String matchWinner){

            Room room = roomService.updateGameWinner(roomId,matchWinner);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @PostMapping("/settle-winning-amount/{roomId}")
    public ResponseEntity<?> settleAmountForWinner(@PathVariable String roomId){

            String response = roomService.settleWinnerAmountForPoolAndDealGame(roomId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new StringResponse(response));
    }

    @PostMapping("/settle-amount/point/{roomId}")
    public ResponseEntity<?> settleAmountForPointGameWinner(@PathVariable String roomId) {
        logger.info("Fetching and processing point game result for Room ID: {}", roomId);

        // Call service to fetch and process data
        Map<String, Double> playerRemainingScore = roomService.getPlayerLostScores(roomId);

        // Call existing settle logic with processed data
        String response = roomService.settleAmountForPointGame(roomId, playerRemainingScore);
        return ResponseEntity.status(HttpStatus.CREATED).body(new StringResponse(response));
    }

    @PostMapping("/{roomId}/split-and-win")
    public ResponseEntity<?> settleAmountForPoolGameSplitAndWin(
            @PathVariable String roomId,
            @RequestBody Map<String, Double> playerRemainingScore) {
        logger.info("Split and Win request received for Room ID: {}, Player Scores: {}", roomId, playerRemainingScore);


            // Validate input
            if (playerRemainingScore == null || playerRemainingScore.isEmpty()) {
                return ResponseEntity.badRequest().body("Player remaining scores must not be null or empty.");
            }

            // Call service method
            Room updatedRoom = roomService.settleAmountForPoolGameSplitAndWin(roomId, playerRemainingScore);

            // Build and return response
            return ResponseEntity.ok().body(Map.of(
                    "message", "Split and Win successfully processed.",
                    "updatedRoom", updatedRoom
            ));
    }

    @GetMapping("/room-details/{tournamentId}")
    public ResponseEntity<List<String>> getRoomIdListByTournament(@PathVariable String tournamentId){
        List<String> rooms = roomService.getRoomDetailsForTournament(tournamentId);
        return ResponseEntity.status(HttpStatus.OK).body(rooms);
    }

    @PostMapping("/join-or-create-room")
    public ResponseEntity<?> joinOrCreateRoom(
            @RequestParam int roomSize,
            @RequestParam String roomType,
            @RequestParam String gameMode,
            @RequestParam String gameStatus,
            @RequestParam(required = false) double pointValue,
            @RequestParam String playerId,
            @RequestParam(required = false) int issuedPoint,
            @RequestParam(required = false) int totalRounds,
            @RequestParam(required = false) String entryType,
            @RequestParam(required = false) double entryPrice,
            @RequestParam(required = false) String visibility,
            @RequestHeader("x-player-id") String extractedId
    ) {
        logger.info("Received request to join or create room with parameters: roomSize={}, roomType={}, gameMode={}, gameStatus={}, pointValue={}, playerId={}, issuedPoint={}, totalRounds={}, entryType={}, entryPrice={}, visibility={}",
                roomSize, roomType, gameMode, gameStatus, pointValue, playerId, issuedPoint, totalRounds, entryType, entryPrice, visibility);

            Room room = roomService.joinOrCreateRoom(
                    roomSize, roomType, gameMode, gameStatus, pointValue,
                    playerId, issuedPoint, totalRounds, entryType, entryPrice, visibility, extractedId
            );

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @PutMapping("/update-9/tournament/{roomId}")
    public ResponseEntity<?> updateRoomForTournament(@PathVariable String roomId,
                                                     @RequestParam List<String> playerIds) {
        Room room = roomService.updateRoomForNinePlayerForTournament(roomId, playerIds);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @GetMapping("/get-room-details/player-last-ten-match")
    public ResponseEntity<List<Room>> getRoomDetailsByPlayerIdForLastTenMatch(@RequestParam String playerId,
                                                                              @RequestHeader("x-player-id") String extractedId){
            List<Room> roomList = roomService.getLastTenMatchDetailsByPlayerId(playerId,extractedId);
            return ResponseEntity.ok(roomList);
    }

    @PatchMapping("/update-last-game/{roomId}")
    public ResponseEntity<List<String>> updateLastGameStatusForRoom(
            @PathVariable String roomId, @RequestParam String playerId, @RequestHeader("x-player-id") String extractedId) {

        Room room = roomService.getRoomAndUpdateLastGamePlayer(roomId, playerId, extractedId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room.getLastGame());
    }

    @PatchMapping("/remove-last-game/{roomId}")
    public ResponseEntity<List<String>> removeLastGameStatusForRoom(
            @PathVariable String roomId, @RequestParam String playerId, @RequestHeader("x-player-id") String extractedId) {

        Room room = roomService.getRoomAndRemovePlayerFromLastGamePlayer(roomId, playerId,extractedId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room.getLastGame());
    }

    @PatchMapping("/update-player-status/{roomId}")
    public ResponseEntity<List<String>> updateActiveStatusForRoom(
            @PathVariable String roomId, @RequestParam String playerId, @RequestHeader String extractedId) {
        Room room = roomService.getRoomAndUpdatePlayerActiveStatus(roomId, playerId, extractedId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room.getIsNotActive());
    }

    @PatchMapping("/remove-player-status/{roomId}")
    public ResponseEntity<List<String>> removeActiveStatusForRoom(
            @PathVariable String roomId, @RequestParam String playerId, @RequestHeader("x-player-id") String extractedId) {
        Room room = roomService.getRoomAndRemovePlayerInActiveStatus(roomId, playerId, extractedId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room.getIsNotActive());
    }

    @PutMapping("/update-totalPrice")
    public ResponseEntity<Room> updateTotalPrice(@RequestParam String roomId, @RequestParam double totalPrice){
        Room room = roomService.updateTotalPrice(roomId,totalPrice);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @GetMapping("/get-room/{playerId}/status/{gameStatus}")
    public ResponseEntity<List<Room>> getRoomDetailsForPlayerId(@PathVariable String playerId, @PathVariable String gameStatus){
        List<Room> roomList = roomService.getRoomsByPlayerIdAndGameStatus(playerId,gameStatus);
        return ResponseEntity.status(HttpStatus.OK).body(roomList);
    }

    @PatchMapping("/update-exit-status/{roomId}")
    public ResponseEntity<List<String>> updateExitStatusForRoom(
            @PathVariable String roomId, @RequestParam String playerId, @RequestHeader("x-player-id") String extractedId) {

        Room room = roomService.getRoomAndUpdatePlayerExitStatus(roomId, playerId, extractedId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room.getExitPlayer());
    }

    @GetMapping("/get-room/status/{gameStatus}")
    public ResponseEntity<List<Room>> getRoomByStatus(@PathVariable String gameStatus){
        List<Room> rooms = roomService.getCompletedGames(gameStatus);
        return ResponseEntity.status(HttpStatus.OK).body(rooms);
    }

    @PutMapping("/update-current-turn/{roomId}")
    public ResponseEntity<Room> updatePlayerTurn(@PathVariable String roomId, @RequestParam String playerId){
        Room room = roomService.updateCurrentPlayerStatus(roomId, playerId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
    }

    @GetMapping("/player-games")
    public List<Room> getPlayerRooms(
            @RequestParam String playerId,
            @RequestParam int roomSize,
            @RequestParam String roomType,
            @RequestParam int issuedPoint) {

        return roomService.getPlayerRooms(playerId, roomSize, roomType, issuedPoint);
    }

    @PostMapping("/send-invite")
    public ResponseEntity<String> sentInviteForFriendsMatch(@RequestBody GameInvite gameInvite, @RequestHeader("x-player-id")String extractedId){
        String response = roomService.inviteFriends(gameInvite,extractedId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/invitation")
    public ResponseEntity<List<InviteResponse>> gameInvite(@RequestParam String playerId, @RequestParam String inviteStatus){
        List<InviteResponse> gameInvites = roomService.getGameDetailsByPlayerId(playerId,Status.valueOf(inviteStatus.toUpperCase()));
        return ResponseEntity.status(HttpStatus.OK).body(gameInvites);
    }

    @PostMapping("/{roomId}/manual-start")
    public ResponseEntity<String> manuallyStartGame(@PathVariable String roomId, @RequestHeader("x-player-id") String extractedId) {
        String response = roomService.manualStartPrivateRoom(roomId,extractedId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-invitation-status")
    public ResponseEntity<String> updateInviteStatus(
            @RequestParam String roomId,
            @RequestParam String playerId,
            @RequestParam String inviteStatus,
            @RequestHeader("x-player-id") String extractedId) {

        String message = roomService.updateGameInviteStatus(roomId, playerId, Status.valueOf(inviteStatus.toUpperCase()), extractedId);
        return ResponseEntity.ok(message);
    }


}
