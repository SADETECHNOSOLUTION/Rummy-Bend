package com.sadetech.point_allocation.service;

import com.sadetech.point_allocation.dto.RequestDTO;
import com.sadetech.point_allocation.dto.RoomDto;
import com.sadetech.point_allocation.dto.Wallet;
import com.sadetech.point_allocation.exception.*;
import com.sadetech.point_allocation.feign.*;
import com.sadetech.point_allocation.model.PlayerData;
import com.sadetech.point_allocation.model.Point;
import com.sadetech.point_allocation.repository.PointRepo;
import lombok.extern.flogger.Flogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PointService {

    @Autowired
    private PointRepo pointRepository;

    @Autowired
    private RoomFeignClient roomFeignClient;

    @Autowired
    private RoomStatusUpdateFeignClient roomStatusUpdateFeignClient;

    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    @Autowired
    private RoomWinnerFeignClient roomWinnerFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private UpdateMoneyFeignClient updateMoneyFeignClient;

    @Autowired
    private WalletDetailsFeignClient walletDetailsFeignClient;

    @Autowired
    private CompanyWalletFeignClient companyWalletFeignClient;

    @Autowired
    private RoomPriceUpdateFeignClient roomPriceUpdateFeignClient;

    @Autowired
    private InGameWalletFeignClient inGameWalletFeignClient;

    @Value("${wallet.id}")
    private String walletId;

    @Autowired
    private RejoinService rejoinService;

    public Point savePointData(Map<String, Object> validationResult, String declaredPlayerId, int exceedPoint) {
        String roomId = (String) validationResult.get("roomId");
        RoomDto roomDto = roomFeignClient.getRoomDetails(roomId);

        int round = roomDto.getCurrentRound();

        double totalLostPoints = (double) validationResult.get("totalLostPoints");
        List<Map<String, Object>> playerScores = (List<Map<String, Object>>) validationResult.get("playerScores");

        if (playerScores == null || playerScores.isEmpty()) {
            throw new ResourceNotFoundException("Player scores are missing or empty");
        }

        Map<String, PlayerData> players = new HashMap<>();
        List<String> activePlayers = new ArrayList<>();
        List<String> eliminatedPlayers = new ArrayList<>();
        Map<String, Double> activePlayerLostPoints = new HashMap<>();

        Point previousPoint = (round > 1) ? pointRepository.findByRoomIdAndRound(roomId, round - 1) : null;

        for (Map<String, Object> playerScore : playerScores) {
            if (!playerScore.containsKey("playerId") || !playerScore.containsKey("remainingPoints") ||
                    !playerScore.containsKey("lostPoints") || !playerScore.containsKey("totalScore")) {
                throw new IllegalArgumentException("Invalid player score structure");
            }

            String playerId = (String) playerScore.get("playerId");
            double remainingPoints = (double) playerScore.get("remainingPoints");
            double currentLostPoints = (double) playerScore.get("lostPoints");
            double totalScore = (double) playerScore.get("totalScore");

            double cumulativeLostPoints = 0.0;
            if (previousPoint != null && previousPoint.getPlayers() != null) {
                PlayerData previousPlayerData = previousPoint.getPlayers().get(playerId);
                if (previousPlayerData != null) {
                    cumulativeLostPoints = Double.parseDouble(previousPlayerData.getPlayerLostScore());
                }
            }

            double updatedLostPoints = cumulativeLostPoints + currentLostPoints;

            if (playerId.equals(declaredPlayerId) && ("Point".equals(roomDto.getRoomType()) || "Deal".equals(roomDto.getRoomType()))) {
                remainingPoints += totalLostPoints;
            }

            if ("Pool".equals(roomDto.getRoomType()) && roomDto.getGameStatus().equals("Ongoing")) {
                if (updatedLostPoints > exceedPoint) {
                    logger.info("Player {} exceeds the point limit of {}", playerId, exceedPoint);
                    eliminatedPlayers.add(playerId);
                    continue;
                } else {
                    activePlayers.add(playerId);
                    activePlayerLostPoints.put(playerId, updatedLostPoints);
                }
            }

            PlayerData playerData = new PlayerData();
            playerData.setTotalScore(String.valueOf(totalScore));
            playerData.setPlayerRemainingScore(String.valueOf(remainingPoints));
            playerData.setPlayerLostScore(String.valueOf(updatedLostPoints));
            players.put(playerId, playerData);
        }

        // Pool Game: Check if eliminated players can rejoin
        if ("Pool".equals(roomDto.getRoomType()) && !eliminatedPlayers.isEmpty() && activePlayers.size() > 2) {
            double highestLostPoint = activePlayerLostPoints.values().stream().max(Double::compareTo).orElse(0.0);
            double rejoinThreshold = exceedPoint - highestLostPoint;

            if (rejoinThreshold > 20) {
                for (String eliminatedPlayer : eliminatedPlayers) {
                    // Send a request to the frontend and wait for response
                    boolean wantsToRejoin = checkPlayerRejoin(eliminatedPlayer, roomId);

                    if (wantsToRejoin) {
                        double entryAmount = getEntryAmount(roomId);
                        double companyFee = entryAmount * 0.15;
                        double playerRejoinScore = highestLostPoint + 1;

                        deductFromPlayerWallet(eliminatedPlayer, entryAmount, roomId);
                        addToCompanyWallet(companyFee);

                        PlayerData rejoinedPlayerData = new PlayerData();
                        rejoinedPlayerData.setTotalScore(String.valueOf(roomDto.getIssuedPoint()));
                        rejoinedPlayerData.setPlayerRemainingScore(String.valueOf(roomDto.getIssuedPoint() - playerRejoinScore));
                        rejoinedPlayerData.setPlayerLostScore(String.valueOf(playerRejoinScore));
                        players.put(eliminatedPlayer, rejoinedPlayerData);
                        activePlayers.add(eliminatedPlayer);
                    }
                }
            }
        }

        Point point = new Point();

        try {
            if ("Point".equals(roomDto.getRoomType()) && round == 1 && (roomDto.getGameMode().equals("Practice") || roomDto.getGameMode().equals("Cash"))) {
                point.setGameStatus("Finished");
                roomStatusUpdateFeignClient.updateGameStatus(roomId, "Finished");
                roomWinnerFeignClient.updateGameWinner(roomId, getWinnerPlayerId(players));
                pointRepository.save(point);
            }

            if ("Deal".equals(roomDto.getRoomType()) && round < roomDto.getTotalRounds()) {
                point.setGameStatus("Ongoing");
            }

            if ("Deal".equals(roomDto.getRoomType()) && round == roomDto.getTotalRounds() ) {
                point.setGameStatus("Finished");
                roomStatusUpdateFeignClient.updateGameStatus(roomId, "Finished");
                roomWinnerFeignClient.updateGameWinner(roomId, getWinnerPlayerId(players));
                pointRepository.save(point);
            }

            if ("Pool".equals(roomDto.getRoomType()) && activePlayers.size() > 1) {
                point.setGameStatus("Ongoing");
            }

            if ("Pool".equals(roomDto.getRoomType()) && activePlayers.size() == 1) {
                point.setGameStatus("Finished");
                roomWinnerFeignClient.updateGameWinner(roomId, activePlayers.get(0));
                roomStatusUpdateFeignClient.updateGameStatus(roomId, "Finished");
                pointRepository.save(point);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating room details", e);
        }

        point.setRoomId(roomId);
        point.setRound(round);
        point.setDeclaredPlayerId(declaredPlayerId);
        point.setExceedPoint(exceedPoint);
        point.setPlayers(players);
        point.setActivePlayers(activePlayers);

        return pointRepository.save(point);
    }

    private boolean checkPlayerRejoin(String playerId, String roomId) {
        try {
            return rejoinService.getRejoinDecision(playerId, roomId); // Call frontend API
        } catch (Exception e) {
            logger.error("Error checking rejoin decision for player {}", playerId, e);
            return false;
        }
    }

    private double getEntryAmount(String roomId) {
        RoomDto roomDto = roomFeignClient.getRoomDetails(roomId);
        if (roomDto == null) {
            throw new RoomNotFoundException("Room details not found for roomId: " + roomId);
        }
        return roomDto.getEntryPrice();
    }

    private void deductFromPlayerWallet(String playerId, double amount, String roomId) {
        RequestDTO requestDTO = userFeignClient.getDetails(playerId);
        RoomDto roomDto = roomFeignClient.getRoomDetails(roomId);

        if (requestDTO == null || roomDto == null) {
            throw new ResourceNotFoundException("Player or Room details not found");
        }

        Optional<Wallet> walletOpt = walletDetailsFeignClient.getWalletDetails(walletId);
        if (walletOpt.isEmpty()) {
            throw new WalletNotFoundException("Wallet not found for wallet id: " + walletId);
        }

        Wallet wallet = walletOpt.get();
        double inGameAmount = requestDTO.getInGameWallet();

        if (amount > inGameAmount) {
            throw new InsufficientMoneyException("Not enough money to rejoin");
        }

        updateMoneyFeignClient.updateInGameMoney(playerId, inGameAmount - amount);
        inGameWalletFeignClient.updateInGameWallet(walletId, wallet.getInGameWallet() - amount);
    }

    private void addToCompanyWallet(double amount) {

        Optional<Wallet> walletOpt = walletDetailsFeignClient.getWalletDetails(walletId);

        if (walletOpt.isEmpty()) {
            throw new WalletNotFoundException("Wallet not found for wallet id : " +  walletId);
        }

        Wallet wallet = walletOpt.get();

        // Update the company wallet
        companyWalletFeignClient.updateCompanyWallet(walletId, wallet.getCompanyWallet() + amount);
    }

    private String getWinnerPlayerId(Map<String, PlayerData> players) {
        String winnerPlayerId = null;
        double maxRemainingScore = -1;

        for (Map.Entry<String, PlayerData> entry : players.entrySet()) {
            double remainingScore = Double.parseDouble(entry.getValue().getPlayerRemainingScore());
            if (remainingScore > maxRemainingScore) {
                maxRemainingScore = remainingScore;
                winnerPlayerId = entry.getKey();
            }
        }
        return winnerPlayerId;
    }

    public List<Point> getByRoomId(String roomId,String gameStatus){
        return pointRepository.findByRoomIdAndGameStatus(roomId,gameStatus);
    }

    public List<Point> getResultByRoomId(String roomId) {
        return pointRepository.findByRoomId(roomId);
    }

    public Point updatePointData(Map<String, Object> validationResult, int round, String declaredPlayerId) {
        String roomId = (String) validationResult.get("roomId");
        Point existingPoint = (Point) pointRepository.findByRoomId(roomId); // Fetch the existing point data by roomId

        if (existingPoint != null) {
            // If the round in the request is different from the current round, update the round
            if (existingPoint.getRound() != round) {
                existingPoint.setRound(round);  // Update the round with the new one
            }

            // Update the players' scores
            double totalLostPoints = (double) validationResult.get("totalLostPoints");
            Map<String, PlayerData> players = existingPoint.getPlayers();

            for (Map<String, Object> playerScore : (List<Map<String, Object>>) validationResult.get("playerScores")) {
                String playerId = (String) playerScore.get("playerId");
                double remainingPoints = (double) playerScore.get("remainingPoints");

                // Add totalLostPoints to the remaining points of the declared player
                if (playerId.equals(declaredPlayerId)) {
                    remainingPoints += totalLostPoints;
                }

                PlayerData playerData = players.get(playerId);
                if (playerData != null) {
                    playerData.setPlayerRemainingScore(String.valueOf(remainingPoints));
                    playerData.setPlayerLostScore(String.valueOf(playerScore.get("lostPoints")));
                    playerData.setTotalScore(String.valueOf(playerScore.get("totalScore")));
                }
            }

            // Save the updated point data back to the database
            return pointRepository.save(existingPoint);
        }

        // If no existing point data is found, return null or handle appropriately
        return null;
    }
}
