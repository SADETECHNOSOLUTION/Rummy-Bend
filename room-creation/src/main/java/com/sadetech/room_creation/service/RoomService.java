package com.sadetech.room_creation.service;

import com.sadetech.room_creation.dto.*;
import com.sadetech.room_creation.exception.*;
import com.sadetech.room_creation.feign.*;
import com.sadetech.room_creation.model.*;
import com.sadetech.room_creation.repository.GameInviteRepository;
import com.sadetech.room_creation.repository.RoomRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private UpdateChipsFeignClient updateChipsFeignClient;

    @Autowired
    private UpdateMoneyFeignClient updateMoneyFeignClient;

    @Autowired
    private CardInitializer6FeignClient cardInitializer6FeignClient;

    @Autowired
    private CardInitializer9FeignClient cardInitializer9FeignClient;

    @Autowired
    private UpdateCashGameFeignClient updateCashGameFeignClient;

    @Autowired
    private CompanyWalletFeignClient companyWalletFeignClient;

    @Autowired
    private InGameWalletFeignClient inGameWalletFeignClient;

    @Autowired
    private WalletDetailsFeignClient walletDetailsFeignClient;

    @Autowired
    private WithDrawMoneyFeignClient withDrawMoneyFeignClient;

    @Autowired
    private WinningWalletFeignClient winningWalletFeignClient;

    @Autowired
    private MissionFeignClient missionFeignClient;

    @Autowired
    private PointGameClient pointGameClient;

    @Value("${wallet.id}")
    private String walletId;

    @Autowired
    private GameInviteRepository gameInviteRepository;

    // private SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room joinOrCreateRoom(int roomSize, String roomType, String gameMode, String gameStatus, double pointValue,
                                 String playerId, int issuedPoint, int totalRounds, String entryType, double entryPrice,
                                 String visibility, String extractedId) {

        logger.info("Joining or creating room for playerId: {}", playerId);
        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player ID should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if(roomSize != 2 && roomSize != 6 && roomSize != 9){
            throw new IllegalArgumentException("Room size only need to be 2,6 or 9");
        }

        Set<String> room_Type = Set.of("point", "pool", "deal");
        if(!room_Type.contains(roomType.toLowerCase())){
            throw new IllegalArgumentException("Room type only need to be point, pool or deal");
        }

        if(!gameMode.equalsIgnoreCase("Practice") && !gameMode.equalsIgnoreCase("Cash")){
            throw new IllegalArgumentException("Game mode need to be only Practice or Cash");
        }

        if(!entryType.equalsIgnoreCase("Chips") && !entryType.equalsIgnoreCase("Money")){
            throw new IllegalArgumentException("Entry type need to be only Chips or Money");
        }

        if(!visibility.equalsIgnoreCase("Public") && !visibility.equalsIgnoreCase("Private")){
            throw new IllegalArgumentException("Visibility need to be only Public or Private");
        }

        // Fetch player details
        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                        .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));
        logger.info("Fetched player details: {}", requestDTO);

        // Validate player balance if entryType is "Money"
        if ("Money".equalsIgnoreCase(entryType) && entryPrice > 0) {
            if (requestDTO.getInGameWallet() < entryPrice) {
                throw new IllegalStateException("Insufficient balance in wallet to join or create the room.");
            }
        }

        // Validate player balance if entryType is "Chips"
        if ("Chips".equalsIgnoreCase(entryType) && entryPrice > 0) {
            if (requestDTO.getChips() < entryPrice) {
                throw new IllegalStateException("Insufficient chips in wallet to join or create the room.");
            }
        }

        // Search for an existing room
        Optional<Room> existingRoom = getRoom(roomSize, roomType, gameMode, gameStatus, pointValue);

        Room room;

        if (existingRoom.isPresent()) {
            logger.info("Found existing room: {}", existingRoom.get());
            room = existingRoom.get();

            // Check if player is already in the room
            if (isPlayerAlreadyInRoom(room, playerId)) {
                throw new IllegalStateException("Player is already in this room and cannot join again.");
            }

            room = updateRoomBasedOnSize(room, gameStatus, playerId, extractedId);
        } else {
            logger.info("No existing room found, creating a new one");
            room = createNewRoom(roomSize, roomType, gameMode, gameStatus, pointValue, issuedPoint, totalRounds, entryType, entryPrice, visibility, requestDTO, extractedId);
        }

        return room;
    }

    private boolean isPlayerAlreadyInRoom(Room room, String playerId) {

        return room.getPlayerDetails() != null && room.getPlayerDetails().stream()
                .anyMatch(player -> player.getPlayerId().equals(playerId));
    }


    private Room updateRoomBasedOnSize(Room room, String gameStatus, String playerId,String extractedId) {
        logger.info("Updating room based on size: {}", room.getRoomSize());

        // Ensure playerDetails list is initialized
        if (room.getPlayerDetails() == null) {
            room.setPlayerDetails(new ArrayList<>());
        }

        return switch (room.getRoomSize()) {
            case 2 -> updateRoomForTwoPlayer(room.getRoomId(), gameStatus, playerId, extractedId);
            case 6 -> updateRoomForSixPlayer(room.getRoomId(), gameStatus, playerId, extractedId);
            case 9 -> updateRoomForNinePlayer(room.getRoomId(), gameStatus, playerId, extractedId);
            default -> throw new IllegalStateException("Unsupported room size: " + room.getRoomSize());
        };
    }


    private Room createNewRoom(int roomSize, String roomType, String gameMode, String gameStatus, double pointValue, int issuedPoint,
                               int totalRounds, String entryType, double entryPrice, String visibility, RequestDTO requestDTO, String extractedId) {

        Room newRoom = new Room();
        newRoom.setRoomSize(roomSize);
        newRoom.setRoomType(roomType);
        newRoom.setGameMode(gameMode);
        newRoom.setGameStatus(gameStatus);
        newRoom.setPointValue(pointValue);
        newRoom.setIssuedPoint(issuedPoint);
        newRoom.setTotalRounds(totalRounds);
        newRoom.setEntryType(entryType);
        newRoom.setEntryPrice(entryPrice);
        newRoom.setVisibility(visibility);

        logger.info("Creating new room: {}", newRoom);

        if ("Money".equalsIgnoreCase(entryType) && entryPrice > 0 && requestDTO.getInGameWallet() < entryPrice) {
            throw new IllegalStateException("Insufficient balance in wallet to create the room.");
        }

        if ("Chips".equalsIgnoreCase(entryType) && entryPrice > 0 && requestDTO.getChips() < entryPrice) {
            throw new IllegalStateException("Insufficient balance in wallet to create the room.");
        }

        Room createdRoom = roomRepository.save(newRoom);
        return updateRoomBasedOnSize(createdRoom, gameStatus, requestDTO.getPlayerId(),extractedId);
    }

    public Optional<Room> getRoom(int roomSize, String roomType, String gameMode, String gameStatus, double pointValue) {
        List<Room> rooms = roomRepository.findByRoomSizeAndRoomTypeAndGameModeAndGameStatusAndPointValue(roomSize, roomType, gameMode, gameStatus, pointValue);
        if (rooms.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rooms.getFirst());
    }

    public Room updateRoomForNinePlayer(String roomId, String gameStatus, String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if (!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        if(!"Waiting".equalsIgnoreCase(gameStatus) &&
                !"Matchmaking".equalsIgnoreCase(gameStatus) &&
                !"Tossing".equalsIgnoreCase(gameStatus) &&
                !"Started".equalsIgnoreCase(gameStatus) &&
                !"Ongoing".equalsIgnoreCase(gameStatus) &&
                !"Finished".equalsIgnoreCase(gameStatus)){
            throw new InvalidGameStatusException("Invalid game status.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        // Fetch player details
        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player id found for the player"));

        // Check if the entry type is "Cash" and validate player's balance
        if ("Money".equalsIgnoreCase(room.getEntryType()) && room.getEntryPrice() > 0) {
            if (requestDTO.getInGameWallet() < room.getEntryPrice()) {
                throw new IllegalStateException("Player with ID " + playerId + " does not have enough money to join the room.");
            }
        }

        if ("Chips".equalsIgnoreCase(room.getEntryType()) && room.getEntryPrice() > 0) {
            if (requestDTO.getChips() < room.getEntryPrice()) {
                throw new IllegalStateException("Player with ID " + playerId + " does not have enough chips to join the room.");
            }
        }

        // Set game status if provided
            room.setGameStatus(gameStatus);

        // Initialize playerDetails list if null
        if (room.getPlayerDetails() == null) {
            room.setPlayerDetails(new ArrayList<>());
        }

        // Check if the room has an available slot
        if (room.getPlayerDetails().size() >= 9) {
            throw new IllegalStateException("No available slot for the player in this room.");
        }

        // Add player to the list
        room.getPlayerDetails().add(new PlayerDetails(playerId, room.getIssuedPoint()));

        // Update player count
        room.setPlayerCount(room.getPlayerDetails().size());

        // Check if the room is ready for matchmaking
        if(room.getVisibility().equals("Public")) {
            if (room.getPlayerCount() >= 2 && room.getPlayerCount() <= 9) {
                scheduler.schedule(() -> {
                    room.setGameStatus("Matchmaking");
                    scheduleGameStart(room);
                }, 20, TimeUnit.SECONDS);
            }
        }else if ("Private".equalsIgnoreCase(room.getVisibility())) {
            if (room.getPlayerCount() == room.getRoomSize()) {
                room.setGameStatus("Matchmaking");
                roomRepository.save(room);
                scheduler.schedule(() -> scheduleGameStart(room), 20, TimeUnit.SECONDS);
            }
        }

        return roomRepository.save(room);
    }

    public Room updateRoomForSixPlayer(String roomId, String gameStatus, String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if (!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        if(!"Waiting".equalsIgnoreCase(gameStatus) &&
                !"Matchmaking".equalsIgnoreCase(gameStatus) &&
                !"Tossing".equalsIgnoreCase(gameStatus) &&
                !"Started".equalsIgnoreCase(gameStatus) &&
                !"Ongoing".equalsIgnoreCase(gameStatus) &&
                !"Finished".equalsIgnoreCase(gameStatus)){
            throw new InvalidGameStatusException("Invalid game status.");
        }

        // Fetch the room from the repository
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        // Fetch player details
        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

        // Check if the entry type is "Cash" and validate player's balance
        if ("Money".equalsIgnoreCase(room.getEntryType()) && room.getEntryPrice() > 0) {
            if (requestDTO.getInGameWallet() < room.getEntryPrice()) {
                throw new IllegalStateException("Player with ID " + playerId + " does not have enough money to join the room.");
            }
        }

        if ("Chips".equalsIgnoreCase(room.getEntryType()) && room.getEntryPrice() > 0) {
            if (requestDTO.getChips() < room.getEntryPrice()) {
                throw new IllegalStateException("Player with ID " + playerId + " does not have enough money to join the room.");
            }
        }

        // Update game status if provided
            room.setGameStatus(gameStatus);

        // Add the player to the list if there's space
        if (room.getPlayerDetails().size() < 6) {
            room.getPlayerDetails().add(new PlayerDetails(playerId, room.getIssuedPoint()));
        } else {
            throw new IllegalStateException("No available slot for the player in this room.");
        }

        // Update player count
        room.setPlayerCount(room.getPlayerDetails().size());

        // Check if the room is ready for matchmaking
        if(room.getVisibility().equals("Public")) {
            if (room.getPlayerCount() >= 2 && room.getPlayerCount() <= 6) {
                scheduler.schedule(() -> {
                    room.setGameStatus("Matchmaking");
                    scheduleGameStart(room);
                }, 20, TimeUnit.SECONDS);
            }
        }else if ("Private".equalsIgnoreCase(room.getVisibility())) {
            if (room.getPlayerCount() == room.getRoomSize()) {
                room.setGameStatus("Matchmaking");
                roomRepository.save(room);
                scheduler.schedule(() -> scheduleGameStart(room), 20, TimeUnit.SECONDS);
            }
        }

        return roomRepository.save(room);
    }

    public Room updateRoomForTwoPlayer(String roomId, String gameStatus, String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        if(!"Waiting".equalsIgnoreCase(gameStatus) &&
                !"Matchmaking".equalsIgnoreCase(gameStatus) &&
                !"Tossing".equalsIgnoreCase(gameStatus) &&
                !"Started".equalsIgnoreCase(gameStatus) &&
                !"Ongoing".equalsIgnoreCase(gameStatus) &&
                !"Finished".equalsIgnoreCase(gameStatus)){
            throw new InvalidGameStatusException("Invalid game status.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room not found"));

        // Fetch player details
        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

        // Check if the entry type is "Cash" and validate player's balance
        if ("Money".equalsIgnoreCase(room.getEntryType()) && room.getEntryPrice() > 0) {
            if (requestDTO.getInGameWallet() < room.getEntryPrice()) {
                throw new IllegalStateException("Player with ID " + playerId + " does not have enough money to join the room.");
            }
        }

        if ("Chips".equalsIgnoreCase(room.getEntryType()) && room.getEntryPrice() > 0) {
            if (requestDTO.getChips() < room.getEntryPrice()) {
                throw new IllegalStateException("Player with ID " + playerId + " does not have enough money to join the room.");
            }
        }

        // Update game status if provided
           room.setGameStatus(gameStatus);

        // Add the player to the list if there's space
        if (room.getPlayerDetails().size() < 2) {
            room.getPlayerDetails().add(new PlayerDetails(playerId, room.getIssuedPoint()));
        } else {
            throw new IllegalStateException("No available slot for the player in this room.");
        }

        // Update player count
        room.setPlayerCount(room.getPlayerDetails().size());

        // Check if the room is ready for matchmaking
        if (room.getPlayerCount() == 2) {
            room.setGameStatus("Matchmaking");
            scheduleGameStart(room);
        }

        return roomRepository.save(room);
    }

    public String manualStartPrivateRoom(String roomId, String extractedId) {
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        GameInvite gameInvite = gameInviteRepository.findByRoomId(roomId)
                .orElseThrow(() -> new NotFoundException("No invite found"));

        // ✅ Owner check
        if (!gameInvite.getRoomOwnerId().equals(extractedId)) {
            throw new UnAuthorizedAccessException("Only the room owner can start the game.");
        }

        // ✅ Player count check
        if (room.getPlayerCount() < 2) {
            throw new IllegalStateException("At least 2 players are required to start the game.");
        }

        // ✅ Schedule game
        room.setGameStatus("Matchmaking");
        roomRepository.save(room);
        scheduler.schedule(() -> scheduleGameStart(room), 20, TimeUnit.SECONDS);

        return "Game start scheduled manually";
    }

    @Async
    private void scheduleGameStart(Room room) {
        CompletableFuture.runAsync(() -> {
            try {
                logger.info("Scheduling game start for room: {}", room.getRoomId());

                // Delay "Tossing" status update by 3 seconds
                CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
                    try {
                        logger.info("Game status updated to 'Tossing' for room: {}", room.getRoomId());
                        room.setGameStatus("Tossing");
                        roomRepository.save(room);
                    } catch (Exception e) {
                        logger.error("Error updating game status to 'Tossing': {}", e.getMessage());
                    }
                });

                // Delay "Started" status update by 6 seconds (3s after "Tossing")
                CompletableFuture.delayedExecutor(6, TimeUnit.SECONDS).execute(() -> {
                    try {
                        logger.info("Game status updated to 'Started' for room: {}", room.getRoomId());
                        room.setGameStatus("Started");
                        roomRepository.save(room);
                    } catch (Exception e) {
                        logger.error("Error updating game status to 'Started': {}", e.getMessage());
                    }
                });

                // Delay calling scheduleGameStartAndDebitEntryFee by 9 seconds (3s after "Started")
                CompletableFuture.delayedExecutor(9, TimeUnit.SECONDS).execute(() -> {
                    try {
                        logger.info("Calling scheduleGameStartAndDebitEntryFee for room: {}", room.getRoomId());
                        scheduleGameStartAndDebitEntryFee(room);
                    } catch (Exception e) {
                        logger.error("Error calling scheduleGameStartAndDebitEntryFee: {}", e.getMessage());
                    }
                });

            } catch (Exception e) {
                logger.error("Error in scheduleGameStart: {}", e.getMessage());
            }
        });
    }

    private void scheduleGameStartAndDebitEntryFee(Room room) {
        CompletableFuture.runAsync(() -> {
            try {
                logger.info("Executing scheduleGameStartAndDebitEntryFee for room: {}", room.getRoomId());

                // Handle players
                for (PlayerDetails playerDetails : room.getPlayerDetails()) {
                    String playerId = playerDetails.getPlayerId();
                    logger.info("Processing player: {}", playerId);

                    RequestDTO player = userFeignClient.getDetails(playerId)
                            .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));
                    logger.info("User feign client get details output {}", player.getPlayerId());

                    if ("Chips".equalsIgnoreCase(room.getEntryType())) {
                        if (player.getChips() < room.getEntryPrice()) {
                            logger.warn("Player {} does not have enough chips to join room {}", playerId, room.getRoomId());
                            throw new IllegalArgumentException("Not enough chips to join");
                        }
                        updateChipsFeignClient.updateChips(playerId, player.getChips() - room.getEntryPrice());
                    } else if ("Money".equalsIgnoreCase(room.getEntryType())) {
                        Optional<Wallet> getWallet = walletDetailsFeignClient.getWalletDetails(walletId);
                        if (getWallet.isEmpty()) {
                            logger.warn("No wallet found for ID {}", walletId);
                            throw new IllegalArgumentException("No ID found and no wallet found");
                        }
                        Wallet existingWallet = getWallet.get();

                        if (player.getInGameWallet() > room.getEntryPrice()) {
                            updateMoneyFeignClient.updateInGameMoney(playerId, player.getInGameWallet() - room.getEntryPrice());
                            userFeignClient.wallet(new WalletDto(null,playerId,"Deduct money","Deducting money from in game deposit wallet of player while joining room", LocalDateTime.now(),"Debited",  + room.getEntryPrice(), room.getRoomId()));
                            inGameWalletFeignClient.updateInGameWallet(walletId, existingWallet.getInGameWallet() - room.getEntryPrice());
                            updateCashGameFeignClient.updateCashGameWalletAndLoyaltyPoint(playerId, player.getCashGameWallet() + room.getEntryPrice());
                        } else {
                            throw new IllegalArgumentException("Not enough money to play");
                        }
                    }
                }

                // Update total price and game status
                if ("Money".equalsIgnoreCase(room.getEntryType()) && "Point".equalsIgnoreCase(room.getRoomType())) {
                    double totalAmount = room.getEntryPrice() * room.getPlayerCount();
                    room.setTotalPrice(totalAmount);
                }

                // Update total price and game status
                if ("Money".equalsIgnoreCase(room.getEntryType()) && !"Point".equalsIgnoreCase(room.getRoomType())) {
                    Optional<Wallet> getWallet = walletDetailsFeignClient.getWalletDetails(walletId);
                    if (getWallet.isEmpty()) {
                        throw new IllegalArgumentException("No ID found and no wallet found");
                    }
                    Wallet existingWallet = getWallet.get();

                    double totalAmount = room.getEntryPrice() * room.getPlayerCount();
                    double companyWallet = totalAmount * 0.15; // 15% deduction
                    double reducedTotalPrice = totalAmount - companyWallet; // Remaining 85%

                    companyWalletFeignClient.updateCompanyWallet(walletId, existingWallet.getCompanyWallet() + companyWallet);
                    room.setTotalPrice(reducedTotalPrice);
                }
                else if ("Chips".equalsIgnoreCase(room.getEntryType())) {
                    room.setTotalPrice(room.getEntryPrice() * room.getPlayerCount());
                }

                room.setGameStatus("Ongoing");
                roomRepository.save(room);
                logger.info("Game set to 'Ongoing' for room: {}", room.getRoomId());

            } catch (Exception e) {
                logger.error("Error in scheduleGameStartAndDebitEntryFee: {}", e.getMessage());
            }
        });
    }

    public String shuffleCardIfStarted(String roomId) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Optional<Room> roomOpt = roomRepository.findById(roomId);
        if (roomOpt.isEmpty()) {
            throw new IllegalArgumentException("No room found with ID: " + roomId);
        }
        Room room = roomOpt.get();

        logger.info("Room found: {}, Status: {}, Size: {}", room.getRoomId(), room.getGameStatus(), room.getRoomSize());

        if ("Ongoing".equals(room.getGameStatus())) {
            if (room.getRoomSize() == 2 || room.getRoomSize() == 6) {
                logger.info("Initializing deck for 6-player room: {}", roomId);
                CardDTO cardDTO1 = cardInitializer6FeignClient.initializeDeckForSixPlayer(roomId);
                if (cardDTO1 == null) {
                    throw new IllegalStateException("Failed to initialize deck for 6-player room: " + roomId);
                }
                logger.info("Card initialized for 6-player mode: {}", cardDTO1);
            }
            if (room.getRoomSize() == 9) {
                logger.info("Initializing deck for 9-player room: {}", roomId);
                CardDTO cardDTO2 = cardInitializer9FeignClient.initializeDeckForNinePlayer(roomId);
                if (cardDTO2 == null) {
                    throw new IllegalStateException("Failed to initialize deck for 9-player room: " + roomId);
                }
                logger.info("Card initialized for 9-player mode: {}", cardDTO2);
            }
        } else {
            logger.warn("Room is not in 'Ongoing' state. Current state: {}", room.getGameStatus());
        }
        return "Deck initialized successfully";
    }

    public Room getDetailsByRoomId(String roomId){

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

       Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new RoomNotFoundException("No room found"));

        // messagingTemplate.convertAndSend("/topic/room/" + roomId, room);

        return room;
    }

    public Room updateGameStatus(String roomId, String gameStatus){

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        if(!"Waiting".equalsIgnoreCase(gameStatus) &&
                !"Matchmaking".equalsIgnoreCase(gameStatus) &&
                !"Tossing".equalsIgnoreCase(gameStatus) &&
                !"Started".equalsIgnoreCase(gameStatus) &&
                !"Ongoing".equalsIgnoreCase(gameStatus) &&
                !"Finished".equalsIgnoreCase(gameStatus)){
            throw new InvalidGameStatusException("Invalid game status.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("No room found"));

            room.setGameStatus(gameStatus);

        return roomRepository.save(room);
    }

    public Room updateTotalPrice(String roomId, double totalPrice){

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        if(totalPrice < 0){
            throw new NoEnoughMoneyException("Total price should not be less than 0");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new IllegalArgumentException("No room found"));

            room.setTotalPrice(totalPrice);

        return roomRepository.save(room);
    }

    public Room updateGameWinner(String roomId, String matchWinner) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        if (matchWinner == null || matchWinner.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        matchWinner = matchWinner.trim().replaceAll("\\s+", "");

        if (!matchWinner.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("No room found"));

            room.setMatchWinner(matchWinner);


        if ("Money".equals(room.getEntryType()) && "Cash".equals(room.getGameMode()) && "Point".equals(room.getRoomType())) {
            List<MissionDto> missions = new ArrayList<>();
            try {
                missions = missionFeignClient.getAllMissionByPlayerId(matchWinner);
            } catch (FeignException.NotFound e) {
                logger.warn("No missions found for player: {}", matchWinner);
            }

            if (!missions.isEmpty()) {
                List<MissionDto> eligibleMissions = missions.stream()
                        .filter(m -> Double.compare(m.getEntryAmount(), room.getEntryPrice()) == 0 &&
                                m.getPlayerCount() == room.getRoomSize() &&
                                !"Completed".equalsIgnoreCase(m.getProgress()))
                        .sorted(Comparator.comparingInt(MissionDto::getTotalRound))
                        .toList();

                for (MissionDto mission : eligibleMissions) {
                    missionFeignClient.updateMission(mission.getId());
                    break; // Update only one mission at a time
                }
            }

            List<MissionDto> missionList = new ArrayList<>();
            try {
                missionList = missionFeignClient.getAllDailyChallengesByPlayerId(matchWinner);
            } catch (FeignException.NotFound e) {
                logger.warn("No daily challenges found for player: {}", matchWinner);
            }

            if (!missionList.isEmpty()) {
                List<MissionDto> dailyChallenges = missionList.stream()
                        .filter(m -> Double.compare(m.getEntryAmount(), room.getEntryPrice()) == 0 &&
                                m.getPlayerCount() == room.getRoomSize() &&
                                !"Completed".equalsIgnoreCase(m.getProgress()))
                        .sorted(Comparator.comparingInt(MissionDto::getTotalRound))
                        .toList();

                for (MissionDto mission : dailyChallenges) {
                    missionFeignClient.updateMission(mission.getId());
                    break; // Update only one mission at a time
                }
            }
        }

        return roomRepository.save(room);
    }


    public String settleWinnerAmountForPoolAndDealGame(String roomId){

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("No room id found"));

        String playerId = room.getMatchWinner();
        logger.info("Winner id is {}", playerId);
        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));
        logger.info("User details for the player id is {}", requestDTO);


        if(requestDTO.getPlayerId().isEmpty()){
            throw new IllegalArgumentException("No player found");
        }
        logger.info("Total price for room is {}", room.getTotalPrice());

        if(room.getRoomType().equals("Pool") || room.getRoomType().equals("Deal") && room.getGameStatus().equals("Finished")){
            if(room.getEntryType().equals("Chips")){
                updateChipsFeignClient.updateChips(playerId,room.getTotalPrice() + requestDTO.getChips());
                room.setTotalPrice(0);
                roomRepository.save(room);
            }else if(room.getEntryType().equals("Money")){
                Optional<Wallet> wallet = walletDetailsFeignClient.getWalletDetails(walletId);
                if(wallet.isEmpty()){
                    throw new IllegalArgumentException("No wallet found for the id");
                }
                Wallet withDrawWallet = wallet.get();
                winningWalletFeignClient.updateWinningMoney(playerId,room.getTotalPrice() + requestDTO.getWinningWallet());
                userFeignClient.wallet(new WalletDto(null,playerId,"Award money","Award money to winning withdraw wallet of the player for winning match", LocalDateTime.now(),"Credited", room.getTotalPrice(), roomId));
                withDrawMoneyFeignClient.updateWithDrawWallet(walletId,room.getTotalPrice() + withDrawWallet.getWithDrawWallet());
                room.setTotalPrice(0);
                roomRepository.save(room);
            }
        }
        return "Amount settled for player who won.";
    }

    public Map<String, Double> getPlayerLostScores(String roomId) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        logger.info("Fetching game result from Feign Client for Room ID: {}", roomId);

            // Fetch game result using Feign Client
        List<Map<String, Object>> gameResults = pointGameClient.getPointGameResult(roomId, "Finished");

        if (gameResults == null || gameResults.isEmpty()) {
            throw new IllegalArgumentException("No game results found for room ID: " + roomId);
        }

        // Extract player lost scores from API response
        Map<String, Double> playerRemainingScore = new HashMap<>();
        for (Map<String, Object> result : gameResults) {
            Map<String, Object> players = (Map<String, Object>) result.get("players");

            for (Map.Entry<String, Object> entry : players.entrySet()) {
                String playerId = entry.getKey();
                Map<String, Object> playerData = (Map<String, Object>) entry.getValue();
                double playerLostScore = Double.parseDouble(playerData.get("playerLostScore").toString());

                playerRemainingScore.put(playerId, playerLostScore);
            }
        }

        logger.info("Processed Player Remaining Scores: {}", playerRemainingScore);
        return playerRemainingScore;
    }

    public String settleAmountForPointGame(String roomId, Map<String, Double> playerRemainingScore) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        logger.info("Processing Room ID: {}", roomId);
        logger.info("Player Remaining Scores: {}", playerRemainingScore);

        // Fetch room by ID
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("No room found "));
        logger.info("Fetched Room : {}", room);

        if (room == null || room.getRoomType() == null || room.getGameStatus() == null) {
            throw new IllegalArgumentException("Room data is incomplete: " + room);
        }

        String winnerPlayerId = room.getMatchWinner();
        if (winnerPlayerId == null) {
            throw new IllegalArgumentException("Match winner is not set for room : " + roomId);
        }

        Optional<Wallet> walletOpt = walletDetailsFeignClient.getWalletDetails(walletId);
        Wallet wallet = walletOpt.orElseThrow(() -> new IllegalArgumentException("No wallet found for ID: " + walletId));

        // Check conditions
        if (room.getRoomType().equals("Point") && room.getGameStatus().equals("Finished")) {
            if (room.getEntryType().equals("Chips")) {
                double totalPrice = room.getTotalPrice();
                logger.info("Initial Total Price: {}", totalPrice);

                // Iterate over player remaining scores
                for (Map.Entry<String, Double> entry : playerRemainingScore.entrySet()) {
                    String playerId = entry.getKey();
                    double remainingScore = entry.getValue();
                    logger.info("Processing Player ID: {} with Score: {}", playerId, remainingScore);

                    try {
                        // Get player details
                        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));
                        if (requestDTO == null) {
                            throw new RuntimeException("Player details not found for player ID: " + playerId);
                        }
                        logger.info("Fetched Player Details: {}", requestDTO);

                        double newChips = requestDTO.getChips() + ( ( 80 - remainingScore ) * 10);
                        logger.info("Updating Player Chips: {}", newChips);

                        // Update chips
                        updateChipsFeignClient.updateChips(playerId, newChips);

                        // Update totalPrice
                        totalPrice -= ( 80 - remainingScore ) * 10;
                    } catch (FeignException e) {
                        logger.error("Feign client failed for player ID :  {} - {}", playerId, e.getMessage());
                        throw new RuntimeException("Failed to fetch player details via Feign client", e);
                    } catch (Exception e) {
                        logger.error("Failed to process player : {} - {}", playerId, e.getMessage());
                        throw new RuntimeException("Failed to process player: " + playerId, e);
                    }
                }

                // Save the updated room entity
                logger.info("Updated Total Price: {}", totalPrice);
                RequestDTO requestDTO = userFeignClient.getDetails(winnerPlayerId)
                        .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));
                double chips = requestDTO.getChips();
                updateChipsFeignClient.updateChips(winnerPlayerId,totalPrice + chips);
                room.setTotalPrice(0);
                roomRepository.save(room);

                logger.info("Room Updated Successfully.");
            }

            if (room.getEntryType().equals("Money")) {
                double totalPrice = room.getTotalPrice();
                logger.info("Initial Total Price of Money : {}", totalPrice);

                double totalInGameAmount = 0;

                for (Map.Entry<String, Double> entry : playerRemainingScore.entrySet()) {
                    String playerId = entry.getKey();
                    double lostScore = entry.getValue();
                    double amountToSettle = (80 - lostScore) * room.getPointValue();

                    if (!playerId.equals(winnerPlayerId)) {
                        try {
                            RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                                    .orElseThrow(() -> new PlayerNotFoundException("No player found for player ID: " + playerId));

                            userFeignClient.wallet(new WalletDto(null,playerId,"Award money","Award money to in game deposit wallet of the player for point match after lost the game.", LocalDateTime.now(),"Credited", amountToSettle, roomId));

                            double newMoney = requestDTO.getInGameWallet() + amountToSettle; // Adding the valid points
                            updateMoneyFeignClient.updateInGameMoney(playerId, newMoney);
                            totalInGameAmount += amountToSettle;
                            logger.info("Updated Player {} In-Game Wallet: {}", playerId, newMoney);

                            totalPrice -= amountToSettle; // Deducting from total price
                        } catch (FeignException e) {
                            logger.error("Feign client failed for player ID : {} - {}", playerId, e.getMessage());
                        }
                    }
                }

                // Update company in-game wallet
                inGameWalletFeignClient.updateInGameWallet(walletId, wallet.getInGameWallet() + totalInGameAmount);

                // Update the winner's wallet
                if (totalPrice > 0) {
                    try {
                        RequestDTO requestDTO = userFeignClient.getDetails(winnerPlayerId)
                                .orElseThrow(() -> new PlayerNotFoundException("No player found for winner ID: " + winnerPlayerId));

                        double winnerMoney = requestDTO.getWinningWallet();
                        double balanceAmount = totalPrice - room.getEntryPrice();

                        double companyShare = balanceAmount * 0.15; // Corrected to 15%
                        double balanceWinningAmount = balanceAmount * 0.85; // Corrected to 85%
                        double finalWinnerAmount = room.getEntryPrice() + balanceWinningAmount;

                        userFeignClient.wallet(new WalletDto(null,winnerPlayerId,"Award money","Award money to winning withdraw wallet of the player for point match after won the game.", LocalDateTime.now(),"Credited", finalWinnerAmount, roomId));

                        // Update the company wallet (Platform Fee)
                        companyWalletFeignClient.updateCompanyWallet(walletId, wallet.getCompanyWallet() + companyShare);
                        logger.info("Updated Company Wallet: {}", wallet.getCompanyWallet() + companyShare);

                        // Update winner's winning wallet
                        winningWalletFeignClient.updateWinningMoney(winnerPlayerId, winnerMoney + finalWinnerAmount);
                        withDrawMoneyFeignClient.updateWithDrawWallet(walletId, wallet.getWithDrawWallet() + finalWinnerAmount);
                        logger.info("Updated Winner {} Wallet: {}", winnerPlayerId, finalWinnerAmount);
                    } catch (FeignException e) {
                        logger.error("Failed to update winner’s wallet for {} - {}", winnerPlayerId, e.getMessage());
                    }
                }

                // Final update to room
                room.setTotalPrice(0);
                roomRepository.save(room);

                logger.info("Room Updated Successfully");
            }

        }

        return "Amount settled for players according to points.";
    }

    public Room settleAmountForPoolGameSplitAndWin(String roomId, Map<String, Double> playerRemainingScore) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        // Fetch the room details
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("No room found "));
        logger.info("Fetched Room: {}", room);

        // Validate room details
        if (!"Pool".equalsIgnoreCase(room.getRoomType()) || !"Ongoing".equalsIgnoreCase(room.getGameStatus())) {
            throw new IllegalArgumentException("Invalid room type or game status for Split and Win: " + room);
        }

        int poolPoint = room.getIssuedPoint(); // Pool Point: 101 or 201
        double totalPrizePool = room.getTotalPrice(); // Total Prize Pool
        double entryFee = room.getEntryPrice(); // Entry Fee per player
        double deductedEntryFee = entryFee * 0.85; // Deduct 15% company fee
        logger.info("Pool Point: {}, Total Prize Pool: {}, Entry Fee after 15% deduction: {}", poolPoint, totalPrizePool, deductedEntryFee);

        // Calculate remaining scoots and total scoots
        Map<String, Integer> playerRemainingScoots = new HashMap<>();
        double totalScootAmount = 0;

        for (Map.Entry<String, Double> entry : playerRemainingScore.entrySet()) {
            String playerId = entry.getKey();
            double playerScore = entry.getValue();

            int remainingPoints = Math.max(0, poolPoint - (int) playerScore);
            int remainingScoots = (int) Math.ceil((double) remainingPoints / 20);
            playerRemainingScoots.put(playerId, remainingScoots);

            double scootAmount = remainingScoots * deductedEntryFee;
            totalScootAmount += scootAmount;
            logger.info("Player: {}, Score: {}, Remaining Points: {}, Remaining Scoots: {}, Scoot Amount: ₹{}",
                    playerId, playerScore, remainingPoints, remainingScoots, scootAmount);
        }

        // Calculate remaining prize pool after distributing scoot-based amounts
        double remainingPrizePool = totalPrizePool - totalScootAmount;

        // Ensure remainingPrizePool is not negative
        remainingPrizePool = Math.max(0, remainingPrizePool);

        int totalPlayers = playerRemainingScore.size();

        // Prevent division by zero
        double splitAmount = (totalPlayers > 0) ? (remainingPrizePool / totalPlayers) : 0;

        logger.info("Total Scoot Amount: ₹{}, Adjusted Remaining Prize Pool: ₹{}, Split Amount per Player: ₹{}",
                totalScootAmount, remainingPrizePool, splitAmount);

        // Distribute final amounts to players
        for (Map.Entry<String, Integer> entry : playerRemainingScoots.entrySet()) {
            String playerId = entry.getKey();
            int playerScoots = entry.getValue();
            double scootAmount = playerScoots * deductedEntryFee;
            double finalAmount = scootAmount + splitAmount;

            try {
                // Fetch player details via Feign client
                RequestDTO playerDetails = userFeignClient.getDetails(playerId)
                        .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

                userFeignClient.wallet(new WalletDto(null,playerId,"Award money","Award money to winning withdraw wallet of the player for split and win.", LocalDateTime.now(),"Credited", finalAmount, roomId));

                double newWinningWallet = playerDetails.getWinningWallet() + finalAmount;
                // Update Winning Wallet
                winningWalletFeignClient.updateWinningMoney(playerId, newWinningWallet);

                logger.info("Player: {}, Scoots: {}, Prize Share: ₹{}, New Winning Wallet: ₹{}",
                        playerId, playerScoots, finalAmount, newWinningWallet);
            } catch (FeignException e) {
                logger.error("Feign client failed for player ID: {} - {}", playerId, e.getMessage());
                throw new RuntimeException("Failed to fetch or update player details for player ID: " + playerId, e);
            } catch (Exception e) {
                logger.error("Failed to process player: {} - {}", playerId, e.getMessage());
                throw new RuntimeException("Failed to process player ID: " + playerId, e);
            }
        }

        // Update Withdraw Wallet
        Optional<Wallet> walletOpt = walletDetailsFeignClient.getWalletDetails(walletId);
        if (walletOpt.isEmpty()) {
            throw new IllegalArgumentException("No wallet found for player ID: " + walletId);
        }
        Wallet wallet = walletOpt.get();
        double newWithdrawWallet = wallet.getWithDrawWallet() + room.getTotalPrice();
        withDrawMoneyFeignClient.updateWithDrawWallet(walletId, newWithdrawWallet);

        // Update room with final prize pool and game status
        room.setTotalPrice(remainingPrizePool);
        room.setGameStatus("SPLIT_AND_WIN");
        roomRepository.save(room);

        logger.info("Room updated with new total prize pool: ₹{} and status: SPLIT_AND_WIN", remainingPrizePool);
        return room;
    }


    public List<String> getRoomDetailsForTournament(String tournamentId){

        if (tournamentId == null || tournamentId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        tournamentId = tournamentId.trim().replaceAll("\\s+", "");

        if (!tournamentId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

       List<Room> rooms = roomRepository.findByTournamentId(tournamentId);
        return rooms.stream()
                .map(Room::getRoomId)
                .toList();
    }


    public Room updateRoomForNinePlayerForTournament(String roomId, List<String> playerIds) {

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room with not found"));

        // Use a set to track unique player IDs and prevent duplicates
        Set<String> uniquePlayerIds = new HashSet<>();
        List<PlayerDetails> playerDetailsList = new ArrayList<>();

        for (String playerId : playerIds) {

            if (playerId == null || playerId.isBlank()) {
                throw new IllegalArgumentException("Player id should not be empty.");
            }

            playerId = playerId.trim().replaceAll("\\s+", "");

            if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
                throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
            }


            if ( uniquePlayerIds.add(playerId)) {
                playerDetailsList.add(new PlayerDetails(playerId, room.getIssuedPoint())); // Default issued points as 0, modify if needed
            }
        }

        room.setPlayerDetails(playerDetailsList);
        room.setPlayerCount(playerDetailsList.size());

        if (room.getPlayerCount() >= 2 && room.getPlayerCount() <= 9) {
            room.setGameStatus("Matchmaking");
            scheduleGameStart(room);
        }

        return roomRepository.save(room);
    }


    public List<Room> getLastTenMatchDetailsByPlayerId(String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the given player ID"));

        // 7 days ago from now
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Fetch the latest 10 rooms ordered by creation date (most recent first)
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "roomCreatedAt"));
        List<Room> roomList = roomRepository.findAll(pageRequest).getContent();

        // Filter:
        // - Only rooms created in last 7 days
        // - That include the given player
        return roomList.stream()
                .filter(room -> room.getRoomCreatedAt() != null &&
                        room.getRoomCreatedAt().isAfter(sevenDaysAgo) &&
                        room.getPlayerDetails() != null &&
                        room.getPlayerDetails().stream()
                                .anyMatch(player -> player.getPlayerId().equals(requestDTO.getPlayerId())))
                .collect(Collectors.toList());
    }



    public Room getRoomAndUpdateLastGamePlayer(String roomId, String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No data found for the room"));

        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

        if (Objects.equals(room.getRoomType(), "Point")) {
            if(!room.getLastGame().contains(requestDTO.getPlayerId())){
                room.getLastGame().add(requestDTO.getPlayerId());
            }else {
                throw new IllegalArgumentException("Player already exists: " + requestDTO.getPlayerId());
            }

        }

        return roomRepository.save(room);
    }

    public Room getRoomAndRemovePlayerFromLastGamePlayer(String roomId, String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }


        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No data found for the room"));

        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

        if (Objects.equals(room.getRoomType(), "Point")) {
            if (room.getLastGame().contains(playerId)) {
                room.getLastGame().remove(requestDTO.getPlayerId());
            }else {
                throw new PlayerNotFoundException("Player not found");
            }
        }

        return roomRepository.save(room);
    }

    public Room getRoomAndUpdatePlayerActiveStatus(String roomId, String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }


        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No data found for the room"));

        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

        if (!room.getIsNotActive().contains(requestDTO.getPlayerId())) {
            room.getIsNotActive().add(requestDTO.getPlayerId());
        }else {
            throw new IllegalArgumentException("Player already exists: " + requestDTO.getPlayerId());
        }

        return roomRepository.save(room);
    }

    public Room getRoomAndRemovePlayerInActiveStatus(String roomId, String playerId, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No data found for the room"));

        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));
        if (room.getIsNotActive().contains(requestDTO.getPlayerId())) {
            room.getIsNotActive().remove(requestDTO.getPlayerId());
        }else {
            throw new PlayerNotFoundException("Player not found");
        }

        return roomRepository.save(room);
    }

    public List<Room> getRoomsByPlayerIdAndGameStatus(String playerId, String gameStatus) {
        try {

            if (playerId == null || playerId.isBlank()) {
                throw new IllegalArgumentException("Player id should not be empty.");
            }

            playerId = playerId.trim().replaceAll("\\s+", "");

            if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
                throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
            }

            if(!"Waiting".equalsIgnoreCase(gameStatus) &&
                    !"Matchmaking".equalsIgnoreCase(gameStatus) &&
                    !"Tossing".equalsIgnoreCase(gameStatus) &&
                    !"Started".equalsIgnoreCase(gameStatus) &&
                    !"Ongoing".equalsIgnoreCase(gameStatus) &&
                    !"Finished".equalsIgnoreCase(gameStatus)){
                throw new InvalidGameStatusException("Invalid game status.");
            }

            List<Room> rooms = roomRepository.findByGameStatus(gameStatus);
            if(rooms.isEmpty()){
                throw new ResourceNotFoundException("No game room found");
            }


            // Fetch player details to validate existence
            RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                    .orElseThrow(() -> new PlayerNotFoundException("No player found for the given player ID"));

            // Fetch all rooms with the given game status
            List<Room> roomList = roomRepository.findByGameStatus(gameStatus);

            // Filter rooms where the player exists in the playerDetails list
            List<Room> filteredRooms = roomList.stream()
                    .filter(room -> room.getPlayerDetails() != null &&
                            room.getPlayerDetails().stream()
                                    .anyMatch(player -> player.getPlayerId().equals(requestDTO.getPlayerId())))
                    .collect(Collectors.toList());

            if (filteredRooms.isEmpty()) {
                throw new ResourceNotFoundException("No room found for the given player ID and game status.");
            }

            return filteredRooms;
        } catch (FeignException e) {
            throw new ServiceUnavailableException("User Service is unavailable, please try again later.");
        }
    }

    public Room getRoomAndUpdatePlayerExitStatus(String roomId, String playerId, String extractedId) {


        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No data found for the room"));

        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

        if (!room.getExitPlayer().contains(requestDTO.getPlayerId())) {
            room.getExitPlayer().add(requestDTO.getPlayerId());
        }else {
            throw new IllegalArgumentException("Player already exists: " + requestDTO.getPlayerId());
        }

        return roomRepository.save(room);
    }

    public List<Room> getCompletedGames(String gameStatus){

        if(!"Waiting".equalsIgnoreCase(gameStatus) &&
                !"Matchmaking".equalsIgnoreCase(gameStatus) &&
                !"Tossing".equalsIgnoreCase(gameStatus) &&
                !"Started".equalsIgnoreCase(gameStatus) &&
                !"Ongoing".equalsIgnoreCase(gameStatus) &&
                !"Finished".equalsIgnoreCase(gameStatus)){
              throw new InvalidGameStatusException("Invalid game status.");
        }
        List<Room> rooms = roomRepository.findByGameStatus(gameStatus);
        if(rooms.isEmpty()){
            throw new ResourceNotFoundException("No game room found");
        }

        return rooms;
    }

    public Room updateCurrentPlayerStatus(String roomId, String playerId){

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("No data found for the room"));

        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found for the player id"));

        room.setCurrentTurn(requestDTO.getPlayerId());
        return roomRepository.save(room);
    }

    public List<Room> getPlayerRooms(String playerId, int roomSize, String roomType, int issuedPoint) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if (roomSize != 2 && roomSize != 6 && roomSize != 9) {
            throw new InvalidRoomSizeException("Invalid room size, Room size should be 2,6 or 9");
        }

        if (!roomType.equalsIgnoreCase("Pool") &&
                !roomType.equalsIgnoreCase("Point") &&
                !roomType.equalsIgnoreCase("Deal")) {
            throw new InvalidRoomTypeException("Invalid room type, Room type should be Deal, Pool or Point");
        }

        if("Point".equalsIgnoreCase(roomType) && issuedPoint != 80){
            throw new InvalidIssuedPointException("Invalid issued point, Issued point should be 80");
        }

        if ("Pool".equalsIgnoreCase(roomType) && issuedPoint != 80 && issuedPoint != 160 && issuedPoint != 240) {
            throw new InvalidIssuedPointException("Invalid issued point for pool, Issued point should be 80, 160 or 240");
        }

        if ("Deal".equalsIgnoreCase(roomType) && issuedPoint != 160 ) {
            throw new InvalidIssuedPointException("Invalid issued point for deal, Issued point should be 160");
        }

        List<Room> roomList = roomRepository.findRoomsByPlayerIdAndAttributes(playerId, roomSize, roomType, issuedPoint);
        if (roomList.isEmpty()) {
            throw new ResourceNotFoundException("No room found for the query");
        }
        return roomList;
    }

    public String inviteFriends(GameInvite gameInvite, String extractedId){

        String roomId = gameInvite.getRoomId();
        String roomOwnerId = gameInvite.getRoomOwnerId();
        List<ParticipantStatus> participantStatusList = gameInvite.getParticipantStatusList();

        if(!extractedId.equals(roomOwnerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if(roomId == null || roomId.isBlank()){
            throw new IllegalArgumentException("Room id should not be empty");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if(!roomId.matches("^[a-fA-F0-9]{24}$")){
            throw new IllegalArgumentException("Room id must be a valid 24 character hexadecimal string");
        }

        if(roomOwnerId.isBlank()){
            throw new IllegalArgumentException("Room owner id should not be empty");
        }

        roomOwnerId = roomOwnerId.trim().replaceAll("\\s+", "");

        if(!roomOwnerId.matches("^[a-fA-F0-9]{24}$")){
            throw new IllegalArgumentException("Room owner id must be a valid 24 character hexadecimal string");
        }

        if(participantStatusList == null || participantStatusList.isEmpty()){
            throw new IllegalArgumentException("Participant list is empty");
        }

        Room room = getDetailsByRoomId(roomId);
        if (!room.getVisibility().equalsIgnoreCase("Private") ||
                !room.getGameStatus().equalsIgnoreCase("Waiting") ||
                !room.getRoomType().equalsIgnoreCase("Pool")) {
            throw new IllegalArgumentException("It is not a private room, status is not 'Waiting', and room type is not 'Pool'.");
        }



        participantStatusList
                        .forEach(participantStatus -> participantStatus.setInviteStatus(Status.INVITED));

        gameInviteRepository.save(gameInvite);
        return "Invite sent successfully for the room id " + roomId;
    }

    public List<InviteResponse> getGameDetailsByPlayerId(String playerId, Status inviteStatus) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        List<GameInvite> gameInvites = gameInviteRepository.findByParticipantStatusList_ParticipantId(playerId);

        if (gameInvites.isEmpty()) {
            throw new InviteNotFoundException("No invite found");
        }

        List<InviteResponse> responseList = new ArrayList<>();

        for (GameInvite invite : gameInvites) {
            // Get this player's status in this invite
            String finalPlayerId = playerId;
            Optional<ParticipantStatus> matchingStatusOpt = invite.getParticipantStatusList().stream()
                    .filter(status -> finalPlayerId.equals(status.getParticipantId()) && status.getInviteStatus() == inviteStatus)
                    .findFirst();

            // Skip this invite if the player doesn't match the given status
            if (matchingStatusOpt.isEmpty()) continue;

            Room room = roomRepository.findById(invite.getRoomId())
                    .orElseThrow(() -> new RoomNotFoundException("Room not found for ID: " + invite.getRoomId()));

            RequestDTO requestDTO = userFeignClient.getDetails(invite.getRoomOwnerId())
                    .orElseThrow(() -> new PlayerNotFoundException("No player found"));

            InviteResponse response = new InviteResponse();
            response.setId(invite.getId());
            response.setRoomId(invite.getRoomId());
            response.setRoomOwnerId(invite.getRoomOwnerId());
            response.setRoomOwnerName(requestDTO.getName());

            // Only add this player's matching status
            response.setParticipantStatusList(List.of(matchingStatusOpt.get()));

            response.setInviteSentAt(invite.getInviteSentAt());
            response.setRoomSize(room.getRoomSize());
            response.setRoomType(room.getRoomType());
            response.setGameMode(room.getGameMode());
            response.setIssuedPoint(room.getIssuedPoint());
            response.setEntryType(room.getEntryType());
            response.setEntryPrice(room.getEntryPrice());

            responseList.add(response);
        }

        if (responseList.isEmpty()) {
            throw new InviteNotFoundException("No invite found for the given player with status: " + inviteStatus);
        }

        return responseList;
    }

    public String updateGameInviteStatus(String roomId, String playerId, Status inviteStatus, String extractedId) {

        if (playerId == null || playerId.isBlank()) {
            throw new IllegalArgumentException("Player id should not be empty.");
        }

        playerId = playerId.trim().replaceAll("\\s+", "");

        if (!playerId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Player ID must be a valid 24-character hexadecimal string.");
        }

        if(!extractedId.equals(playerId)){
            throw new UnAuthorizedAccessException("Access denied");
        }

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room id should not be empty.");
        }

        roomId = roomId.trim().replaceAll("\\s+", "");

        if (!roomId.matches("^[a-fA-F0-9]{24}$")) {
            throw new IllegalArgumentException("Room ID must be a valid 24-character hexadecimal string.");
        }

        GameInvite gameInvite = gameInviteRepository.findByRoomId(roomId)
                .orElseThrow(() -> new InviteNotFoundException("No invite found."));

        boolean updated = false;

        for (ParticipantStatus ps : gameInvite.getParticipantStatusList()) {
            if (ps.getParticipantId().equals(playerId)) {

                // Already accepted
                if (ps.getInviteStatus() == Status.ACCEPTED) {
                    // Run some specific code here
                    return "Player has already accepted the invite.";
                }


                // Already rejected
                if (ps.getInviteStatus() == Status.REJECTED) {
                    return "Player has already rejected the invite.";
                }

                // Status is INVITED, allow update
                ps.setInviteStatus(inviteStatus);
                runPostAcceptLogic(playerId, roomId); // Placeholder
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new InviteNotFoundException("Player not found in the invite list.");
        }

        gameInviteRepository.save(gameInvite);
        return "Invite status updated successfully.";
    }

    // Logic to add player to room if accepted and reject the game invitation
    private void runPostAcceptLogic(String playerId, String roomId) {
        // Retrieve room from repository
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("No room found"));

        // Fetch player details from user service
        RequestDTO requestDTO = userFeignClient.getDetails(playerId)
                .orElseThrow(() -> new PlayerNotFoundException("No player found"));

        // Check if the player has enough money in the wallet to join the room
        if (requestDTO.getInGameWallet() < room.getEntryPrice()) {
            throw new NoEnoughMoneyException("Not enough money. Please recharge your wallet.");
        }

        // Check if room is full
        if (room.getPlayerDetails().size() >= room.getRoomSize()) {
            throw new RoomFullException("Room is already full.");
        }

        List<PlayerDetails> playerDetails = room.getPlayerDetails();

        // Check if player already exists in the room
        boolean alreadyExists = playerDetails.stream()
                .anyMatch(player -> player.getPlayerId().equals(playerId));

        // If player doesn't already exist, add to the room
        if (!alreadyExists) {
            playerDetails.add(new PlayerDetails(playerId, room.getIssuedPoint()));
            room.setPlayerDetails(playerDetails);

            // Save the updated room
            roomRepository.save(room);
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void cleanExpiredRoom(){
        List<Room> rooms = roomRepository.findByGameStatus("Waiting");
        LocalDateTime currentTime = LocalDateTime.now();
        for (Room room : rooms) {
            if(room.getRoomCreatedAt() != null && room.getRoomCreatedAt().plusSeconds(60).isBefore(currentTime)){
                room.setGameStatus("Expired");
                roomRepository.save(room);
                roomRepository.delete(room);
            }
        }
    }

}