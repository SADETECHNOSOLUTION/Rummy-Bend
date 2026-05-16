package com.sadetech.tournament.service;

import com.sadetech.tournament.dto.*;
import com.sadetech.tournament.exceptions.TournamentNotFoundException;
import com.sadetech.tournament.exceptions.UnAuthorizedAccessException;
import com.sadetech.tournament.feign.*;
import com.sadetech.tournament.model.Tournament;
import com.sadetech.tournament.repository.TournamentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepo tournamentRepo;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private RoomCreationFeignClient roomCreationFeignClient;

    @Autowired
    private RoomUpdateFeignClient roomUpdateFeignClient;

    @Autowired
    private PointFeignClient pointFeignClient;

    @Autowired
    private RoomDetailsFeignClient roomDetailsFeignClient;

    @Autowired
    private UserDetailFeignClient userDetailFeignClient;

    @Autowired
    private WalletFeignClient walletFeignClient;

    @Autowired
    private UpdateMoneyFeignClient updateMoneyFeignClient;

    @Autowired
    private WalletDetailsFeignClient walletDetailsFeignClient;

    @Value("${wallet.id}")
    private String walletId;

    public Tournament createTournament(Tournament tournament){
        return tournamentRepo.save(tournament);
    }


    public Tournament getTournamentDetails(String id){
        return tournamentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("No id found"));
    }

    public Tournament updatePlayers(String id, String playerId, String extractedId) {

        Tournament tournament = tournamentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament ID not found"));

        RequestDto requestDto = userDetailFeignClient.getDetails(playerId);

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Un authorized user, Access denied");
        }

        Optional<WalletDTO> walletDTO = walletDetailsFeignClient.getWalletDetails(walletId);
        if(walletDTO.isEmpty()){
            throw new IllegalArgumentException("No wallet found");
        }

        WalletDTO walletDTO1 = walletDTO.get();

        if(tournament.getPlayerId().size() == tournament.getTournamentRoomSize() ||
        tournament.getPlayerId().size() > tournament.getTournamentRoomSize()){
            throw new IllegalArgumentException("Max players joined, Room is full");
        }


        if(tournament.getTournamentType().equals("Cash")){

            if(tournament.getEntryFee() >= requestDto.getInGameWallet()){
                throw new IllegalArgumentException("Not enough money to join the tournament");
            }

            else {
                if(userFeignClient.findUserExistOrNot(playerId)) {

                    updateMoneyFeignClient.updateInGameMoney(playerId, requestDto.getInGameWallet() - tournament.getEntryFee());
                    walletFeignClient.updateInGameWallet(walletId,walletDTO1.getInGameWallet() - tournament.getEntryFee());

                    if (!tournament.getPlayerId().contains(playerId)) {
                        tournament.getPlayerId().add(playerId);
                    } else {
                        throw new IllegalArgumentException("Player is already participating in the tournament");
                    }
                }
            }
        }

        if(tournament.getTournamentType().equals("Free")){
            if(userFeignClient.findUserExistOrNot(playerId)) {

                if (!tournament.getPlayerId().contains(playerId)) {
                    tournament.getPlayerId().add(playerId);
                } else {
                    throw new IllegalArgumentException("Player is already participating in the tournament");
                }
            }
        }

        return tournamentRepo.save(tournament);
    }

    public void updateTournamentStatusToStarted() {
        LocalDateTime now = LocalDateTime.now();
        List<Tournament> tournaments = tournamentRepo.findByTournamentStatusAndMatchStartingAtBefore("Waiting", now);

        for (Tournament tournament : tournaments) {
            tournament.setTournamentStatus("Started");
            tournamentRepo.save(tournament);
        }
    }

    public Tournament createRoomForPlayers(int defaultPoint) {
        // Find tournaments with the given status
        Tournament startedTournament = tournamentRepo.findByTournamentStatus("Started");

        if (startedTournament == null) {
            throw new IllegalArgumentException("Tournament not yet started.");
        }

        List<String> playerIds = startedTournament.getPlayerId();
        int playerIdCount = playerIds.size();

        // Determine the number of rooms to create based on the remainder
        int roomToCreate = (playerIdCount % 9 == 1) ? (playerIdCount / 8 + 1) : (playerIdCount / 9 + (playerIdCount % 9 == 0 ? 0 : 1));

        int playerIndex = 0;

        for (int i = 0; i < roomToCreate; i++) {
            // Step 1: Create a room
            RoomDto roomDto = new RoomDto();
            roomDto.setTournamentId(startedTournament.getId());
            roomDto.setRoomSize(9); // Default room size, adjust if needed
            roomDto.setRoomType(startedTournament.getTournamentMode());
            roomDto.setIssuedPoint(defaultPoint);
            roomDto.setGameMode("Tournament");
            roomDto.setEntryType("Free");
            roomDto.setEntryPrice(startedTournament.getEntryFee());
            RoomDto createdRoom = roomCreationFeignClient.createRoom(roomDto);

            startedTournament.getRoomId().add(createdRoom.getRoomId());

            // Step 2: Assign players to the created room
            String roomId = createdRoom.getRoomId(); // Assuming the created room returns an ID
            String playerId1 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId2 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId3 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId4 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId5 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId6 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId7 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId8 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;
            String playerId9 = (playerIndex < playerIds.size()) ? playerIds.get(playerIndex++) : null;

            roomUpdateFeignClient.updateRoomForTournament(roomId, playerId1, playerId2, playerId3, playerId4, playerId5, playerId6, playerId7, playerId8, playerId9);
        }

        return startedTournament; // Return the updated tournament if needed
    }

    public List<Map.Entry<String, Integer>> getRemainingScoresByRoom(String id) {
        // Fetch the tournament by ID
        Tournament tournament = tournamentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament ID not found"));

        String gameStatus = "Finished"; // Game status to filter
        List<String> roomIds = tournament.getRoomId(); // Assuming room IDs are stored in the tournament

        // Create a map to hold all player scores
        Map<String, Integer> playerRemainingScores = new HashMap<>();

        // Loop through each room ID
        for (String roomId : roomIds) {
            // Call the Feign client to get PointDto for the room
            PointDto pointDto = pointFeignClient.getResult(roomId, gameStatus);

            if (pointDto != null && pointDto.getPlayers() != null) {
                // Extract the player data
                Map<String, PlayerData> players = pointDto.getPlayers();

                for (Map.Entry<String, PlayerData> entry : players.entrySet()) {
                    String playerId = entry.getKey();
                    int remainingScore = Integer.parseInt(entry.getValue().getPlayerRemainingScore());

                    // Add to the result map (sum scores if the player appears in multiple rooms)
                    playerRemainingScores.merge(playerId, remainingScore, Integer::sum);
                }
            }
        }

        // Remove selected players from the map
        List<String> selectedPlayers = tournament.getSelectedPlayers(); // Retrieve the selected players
        if (selectedPlayers != null) {
            for (String selectedPlayer : selectedPlayers) {
                playerRemainingScores.remove(selectedPlayer); // Remove the selected player from the map
            }
        }

        // Sort the scores in descending order and return as a list
        return playerRemainingScores.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // Descending order
                .collect(Collectors.toList());
    }


    public Tournament selectPlayerForNextRound(String id,int minimumQualifyPoint) {
        Tournament tournament = tournamentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tournament ID not found"));

        List<String> roomIds = tournament.getRoomId(); // Assuming room IDs are stored in the tournament
        List<String> selectedPlayers = new ArrayList<>();

        for (String roomId : roomIds) {
            RoomDto roomDto = roomDetailsFeignClient.getRoomDetails(roomId);
            if (roomDto.getGameStatus().equals("Finished") && roomDto.getRoomType().equals("Deal")) {
                selectedPlayers.add(roomDto.getMatchWinner());
            }
            if (roomDto.getGameStatus().equals("Finished") && roomDto.getRoomType().equals("Point")) {
                PointDto pointDto = pointFeignClient.getResult(roomId, roomDto.getGameStatus());

                // Iterate through each player in the players map
                for (Map.Entry<String, PlayerData> entry : pointDto.getPlayers().entrySet()) {
                    PlayerData playerData = entry.getValue();

                    // Convert playerRemainingScore to an integer for comparison
                    int playerRemainingScore = Integer.parseInt(playerData.getPlayerRemainingScore());

                    // Compare playerRemainingScore with minimumQualifyPoint
                    if (playerRemainingScore >= minimumQualifyPoint) {
                        selectedPlayers.add(entry.getKey()); // Add player ID to the selected players list
                    }
                }
            }
        }

        // Assuming you need to update the tournament object with selected players
        tournament.setSelectedPlayers(selectedPlayers); // Add selected players to the tournament
        tournamentRepo.save(tournament); // Optionally save the updated tournament

        return tournament;
    }

    public List<Tournament> getAllTournament() {
        return tournamentRepo.findAll();
    }

    public List<String> getPlayerList(String tournamentId){
        Optional<Tournament> tournament = tournamentRepo.findById(tournamentId);
        if(tournament.isEmpty()){
            throw  new IllegalArgumentException("No details found for the tournament id");
        }
        return tournament.get().getPlayerId();
    }

    public List<String> getSelectedPlayerList(String tournamentId){
        Optional<Tournament> tournament = tournamentRepo.findById(tournamentId);
        if(tournament.isEmpty()){
            throw  new IllegalArgumentException("No details found for the tournament id");
        }
        return tournament.get().getSelectedPlayers();
    }

    public String deleteTournamentById(String id) {
        if (!tournamentRepo.existsById(id)) {
            throw new TournamentNotFoundException("No tournament found for the id: " + id);
        }

        tournamentRepo.deleteById(id);
        return "Tournament deleted successfully";
    }

    public String updateTournamentStatus(String id, String tournamentStatus){
        Tournament tournament = tournamentRepo.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("No tournament found for the id: " + id));

        tournament.setTournamentStatus(tournamentStatus);
        tournamentRepo.save(tournament);
        return "Tournament status updated successfully";
    }

    public String updateTournamentStartingTime(String id, LocalTime matchStartingAt){
        Tournament tournament = tournamentRepo.findById(id)
                .orElseThrow(() -> new TournamentNotFoundException("No tournament found for the id: " + id));

        tournament.setMatchStartingAt(matchStartingAt);
        tournamentRepo.save(tournament);
        return "Tournament time updated successfully";
    }

    public List<Tournament> getLast7DaysTournament() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);

        // Fetch tournaments from the last 7 days, ordered by creation date in descending order
        List<Tournament> tournamentList = tournamentRepo.findByCreatedAtAfterOrderByCreatedAtDesc(sevenDaysAgo);

        if(tournamentList.isEmpty()){
           tournamentList =  Collections.emptyList();
        }

        return tournamentList;
    }

}