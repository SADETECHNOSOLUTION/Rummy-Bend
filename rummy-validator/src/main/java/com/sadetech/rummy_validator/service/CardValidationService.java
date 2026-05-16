package com.sadetech.rummy_validator.service;

import com.sadetech.rummy_validator.dto.CardDTO;
import com.sadetech.rummy_validator.dto.RoomDto;
import com.sadetech.rummy_validator.exception.CardDetailsNotFoundException;
import com.sadetech.rummy_validator.feign.CardFeignClient;
import com.sadetech.rummy_validator.feign.RoomFeignClient;
import com.sadetech.rummy_validator.feign.UserFeignClient;
import com.sadetech.rummy_validator.model.PlayerData;
import com.sadetech.rummy_validator.model.PlayerScore;
import com.sadetech.rummy_validator.model.RummyValidation;
import com.sadetech.rummy_validator.model.RummyValidationResponse;
import com.sadetech.rummy_validator.repository.RummyValidationRepository;
import com.sadetech.rummy_validator.repository.RummyValidationResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardValidationService {

    @Autowired
    private RummyValidationRepository rummyValidationRepository;

    @Autowired
    private RummyValidationResponseRepository responseRepository;

    @Autowired
    private RoomFeignClient roomFeignClient;

    @Autowired
    private CardFeignClient cardFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

//    public Map<String, Object> validateAllPlayersCards(
//            String roomId,
//            Map<String, Map<String, List<String>>> playersCards,
//            Map<String, Double> playerTotalPoints,
//            Map<String, String> playerDropType
//    ) {
//        Map<String, Object> result = new HashMap<>();
//        List<Map<String, Object>> playerScores = new ArrayList<>();
//        Map<String, PlayerData> playersData = new HashMap<>();
//
//        CardDTO cardDTO = cardFeignClient.getCardDetailsByRoomId(roomId)
//                .orElseThrow(() -> new CardDetailsNotFoundException("No card details found for the room Id"));
//
//        Map<String, Integer> playersSteps = cardDTO.getPlayerSteps(); // Steps played by each player
//        double totalLostPoints = 0;
//
//        for (Map.Entry<String, Map<String, List<String>>> playerEntry : playersCards.entrySet()) {
//            String playerId = playerEntry.getKey();
//            Map<String, List<String>> sequenceCard = playerEntry.getValue();
//            double totalPoints = playerTotalPoints.getOrDefault(playerId, 0.0);
//            double dropPoints = 0;
//            String dropType = playerDropType.get(playerId);
//
//            if (dropType != null) {
//                dropPoints = dropType.equals("Drop") ? 20 : (dropType.equals("Middle-Drop") ? 40 : 0);
//            }
//            totalPoints -= dropPoints;
//
//            double lostPoints = 0;
//            if (dropType == null) {
//                for (Map.Entry<String, List<String>> entry : sequenceCard.entrySet()) {
//                    String key = entry.getKey();
//                    List<String> cards = entry.getValue();
//
//                    switch (key) {
//                        case "pureSequence":
//                        case "pureSequence1":
//                        case "pureSequence2":
//                        case "pureSequence3":
//                            lostPoints += validatePureSequence(cards);
//                            break;
//                        case "set":
//                        case "set1":
//                        case "set2":
//                        case "set3":
//                            lostPoints += validateSet(cards);
//                            break;
//                        case "sequence":
//                        case "sequence1":
//                        case "sequence2":
//                        case "sequence3":
//                            lostPoints += validateSequence(cards);
//                            break;
//                        default:
//                            lostPoints += calculateInvalidCardPoints(cards);
//                            break;
//                    }
//                }
//
//                RoomDto roomDto = roomFeignClient.getRoomDetails(roomId);
//                double remainingScore = totalPoints - lostPoints;
//                if ("Pool".equals(roomDto.getRoomType())) {
//                    totalPoints = 0;
//                    remainingScore = lostPoints;
//                }
//
//                int playerStepCount = playersSteps.getOrDefault(playerId, 0);
//                if (playerStepCount == 0) {
//                    lostPoints /= 2;
//                }
//
//                Map<String, Object> playerScore = new HashMap<>();
//                playerScore.put("playerId", playerId);
//                playerScore.put("totalScore", totalPoints);
//                playerScore.put("lostPoints", lostPoints);
//                playerScore.put("remainingPoints", remainingScore);
//                playerScores.add(playerScore);
//
//                playersData.put(playerId, new PlayerData(sequenceCard,
//                        String.valueOf(totalPoints),
//                        String.valueOf(remainingScore),
//                        String.valueOf(lostPoints)));
//
//                totalLostPoints += lostPoints;
//            } else {
//                Map<String, Object> playerScore = new HashMap<>();
//                playerScore.put("playerId", playerId);
//                playerScore.put("totalScore", totalPoints);
//                playerScore.put("lostPoints", dropPoints);
//                playerScore.put("remainingPoints", totalPoints);
//                playerScores.add(playerScore);
//
//                playersData.put(playerId, new PlayerData(sequenceCard,
//                        String.valueOf(totalPoints),
//                        String.valueOf(totalPoints),
//                        String.valueOf(dropPoints)));
//
//                totalLostPoints += dropPoints;
//            }
//        }
//
//        result.put("roomId", roomId);
//        result.put("playerScores", playerScores);
//        result.put("totalLostPoints", totalLostPoints);
//
//        // Save to MongoDB
//        RummyValidation rummyValidation = new RummyValidation();
//        rummyValidation.setRoomId(roomId);
//        rummyValidation.setPlayers(playersData);
//        rummyValidation.setPlayersCards(playersCards);
//        rummyValidation.setPlayerTotalPoints(playerTotalPoints);
//        rummyValidation.setPlayerDropType(playerDropType);
//        rummyValidation.setValidatedAt(LocalDateTime.now());
//
//        // Save total lost points as well
//        rummyValidationRepository.save(rummyValidation);
//
//        RummyValidationResponse validationResponse = new RummyValidationResponse();
//        validationResponse.setRoomId(roomId);
//        List<PlayerScore> playerScoreList = playerScores.stream().map(score ->
//                new PlayerScore(
//                        (String) score.get("playerId"),
//                        (double) score.get("totalScore"),
//                        (double) score.get("lostPoints"),
//                        (double) score.get("remainingPoints")
//                )
//        ).collect(Collectors.toList());
//
//        validationResponse.setPlayerScores(playerScoreList); // ✅ Correct type
//        validationResponse.setTotalLostPoints(totalLostPoints);
//        validationResponse.setValidatedAt(LocalDateTime.now());
//        responseRepository.save(validationResponse);
//
//
//        return result;
//    }

    public Map<String, Object> validateAllPlayersCards(
            String roomId,
            Map<String, Map<String, List<String>>> playersCards,
            Map<String, Double> playerTotalPoints,
            Map<String, String> playerDropType) {

        // Input validations
        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty");
        }

        if (playersCards == null || playersCards.isEmpty()) {
            throw new IllegalArgumentException("Players cards map cannot be null or empty");
        }

        if (playerTotalPoints == null) {
            throw new IllegalArgumentException("Player total points map cannot be null");
        }

        if (playerDropType == null) {
            throw new IllegalArgumentException("Player drop type map cannot be null");
        }

        RoomDto roomDto = roomFeignClient.getRoomDetails(roomId);
        if (roomDto == null) {
            throw new IllegalStateException("Room details not found for ID: " + roomId);
        }

        // Initialize result objects
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> playerScores = new ArrayList<>();
        Map<String, PlayerData> playersData = new HashMap<>();

        // Fetch card details with validation
        CardDTO cardDTO = cardFeignClient.getCardDetailsByRoomId(roomId)
                .orElseThrow(() -> new CardDetailsNotFoundException("No card details found for room ID: " + roomId));

        Map<String, Integer> playersSteps = cardDTO.getPlayerSteps();
        double totalLostPoints = 0;

        // Process each player's cards
        for (Map.Entry<String, Map<String, List<String>>> playerEntry : playersCards.entrySet()) {
            String playerId = playerEntry.getKey();
            Map<String, List<String>> sequenceCard = playerEntry.getValue();

            // Validate player data exists
            if (!playerTotalPoints.containsKey(playerId)) {
                throw new IllegalStateException("Missing total points for player: " + playerId);
            }

            double totalPoints = playerTotalPoints.get(playerId);
            String dropType = playerDropType.get(playerId);

            if (dropType != null) {
                // Handle dropped players
                processDroppedPlayer(playerId, sequenceCard, totalPoints, dropType,
                        playerScores, playersData, totalLostPoints);
            } else {
                // Handle active players
                processActivePlayer(roomId, playerId, sequenceCard, totalPoints,
                        playersSteps, playerScores, playersData, totalLostPoints);
            }
        }

        // Build and save results
        buildAndSaveResults(roomId, playerScores, playersData, playersCards,
                playerTotalPoints, playerDropType, totalLostPoints, result);

        return result;
    }

    private void processDroppedPlayer(String playerId, Map<String, List<String>> sequenceCard,
                                      double totalPoints, String dropType, List<Map<String, Object>> playerScores,
                                      Map<String, PlayerData> playersData, double totalLostPoints) {

        // Validate drop type
        if (!"Drop".equals(dropType) && !"Middle-Drop".equals(dropType)) {
            throw new IllegalArgumentException("Invalid drop type: " + dropType);
        }

        double dropPoints = "Drop".equals(dropType) ? 20 : 40;
        totalPoints -= dropPoints;

        Map<String, Object> playerScore = new HashMap<>();
        playerScore.put("playerId", playerId);
        playerScore.put("totalScore", totalPoints);
        playerScore.put("lostPoints", dropPoints);
        playerScore.put("remainingPoints", totalPoints);
        playerScores.add(playerScore);

        playersData.put(playerId, new PlayerData(sequenceCard,
                String.valueOf(totalPoints),
                String.valueOf(totalPoints),
                String.valueOf(dropPoints)));

        totalLostPoints += dropPoints;
    }

    private void processActivePlayer(String roomId, String playerId,
                                     Map<String, List<String>> sequenceCard, double totalPoints,
                                     Map<String, Integer> playersSteps, List<Map<String, Object>> playerScores,
                                     Map<String, PlayerData> playersData, double totalLostPoints) {

        double lostPoints = 0;

        for (Map.Entry<String, List<String>> entry : sequenceCard.entrySet()) {
            String key = entry.getKey();
            List<String> cards = entry.getValue();

            if (cards == null) {
                throw new IllegalStateException("Card list cannot be null for player " + playerId + " sequence " + key);
            }

            lostPoints += calculateLostPointsForSequence(key, cards);
        }

        RoomDto roomDto = roomFeignClient.getRoomDetails(roomId);
        if (roomDto == null) {
            throw new IllegalStateException("Room details not found for ID: " + roomId);
        }

        double remainingScore = totalPoints - lostPoints;
        if ("Pool".equals(roomDto.getRoomType())) {
            totalPoints = 0;
            remainingScore = lostPoints;
        }

        // Adjust for player steps
        int playerStepCount = playersSteps.getOrDefault(playerId, 0);
        if (playerStepCount == 0) {
            lostPoints /= 2;
        }

        Map<String, Object> playerScore = new HashMap<>();
        playerScore.put("playerId", playerId);
        playerScore.put("totalScore", totalPoints);
        playerScore.put("lostPoints", lostPoints);
        playerScore.put("remainingPoints", remainingScore);
        playerScores.add(playerScore);

        playersData.put(playerId, new PlayerData(sequenceCard,
                String.valueOf(totalPoints),
                String.valueOf(remainingScore),
                String.valueOf(lostPoints)));

        totalLostPoints += lostPoints;
    }

    private double calculateLostPointsForSequence(String sequenceType, List<String> cards) {
        return switch (sequenceType) {
            case "pureSequence", "pureSequence1", "pureSequence2", "pureSequence3" -> validatePureSequence(cards);
            case "set", "set1", "set2", "set3" -> validateSet(cards);
            case "sequence", "sequence1", "sequence2", "sequence3" -> validateSequence(cards);
            default -> calculateInvalidCardPoints(cards);
        };
    }

    private void buildAndSaveResults(String roomId, List<Map<String, Object>> playerScores,
                                     Map<String, PlayerData> playersData, Map<String, Map<String, List<String>>> playersCards,
                                     Map<String, Double> playerTotalPoints, Map<String, String> playerDropType,
                                     double totalLostPoints, Map<String, Object> result) {

        // Build result map
        result.put("roomId", roomId);
        result.put("playerScores", playerScores);
        result.put("totalLostPoints", totalLostPoints);

        // Save to MongoDB
        RummyValidation rummyValidation = new RummyValidation();
        rummyValidation.setRoomId(roomId);
        rummyValidation.setPlayers(playersData);
        rummyValidation.setPlayersCards(playersCards);
        rummyValidation.setPlayerTotalPoints(playerTotalPoints);
        rummyValidation.setPlayerDropType(playerDropType);
        rummyValidation.setValidatedAt(LocalDateTime.now());
        rummyValidationRepository.save(rummyValidation);

        // Create and save response
        RummyValidationResponse validationResponse = new RummyValidationResponse();
        validationResponse.setRoomId(roomId);

        List<PlayerScore> playerScoreList = playerScores.stream()
                .map(score -> new PlayerScore(
                        (String) score.get("playerId"),
                        (double) score.get("totalScore"),
                        (double) score.get("lostPoints"),
                        (double) score.get("remainingPoints")))
                .collect(Collectors.toList());

        validationResponse.setPlayerScores(playerScoreList);
        validationResponse.setTotalLostPoints(totalLostPoints);
        validationResponse.setValidatedAt(LocalDateTime.now());
        responseRepository.save(validationResponse);
    }
















    public Map<String, Map<String, Object>> validateCards(Map<String, List<List<Map<String, String>>>> playerCards, String roomId) {
        Map<String, Map<String, Object>> validationResult = new HashMap<>();

        if (roomId == null || roomId.isBlank()) {
            throw new IllegalArgumentException("Room ID cannot be null or empty");
        }

        RoomDto roomDto = roomFeignClient.getRoomDetails(roomId);
        if (roomDto == null) {
            throw new IllegalStateException("Room details not found for ID: " + roomId);
        }

        // Fetch original card details
        CardDTO cardDTO = cardFeignClient.getCardDetailsByRoomId(roomId)
                .orElseThrow(() -> new CardDetailsNotFoundException("No card details found for room ID: " + roomId));
        Map<String, List<Map<String, String>>> originalCards = cardDTO.getPlayerCards();

        for (Map.Entry<String, List<List<Map<String, String>>>> entry : playerCards.entrySet()) {
            String playerId = entry.getKey();
            List<List<Map<String, String>>> groups = entry.getValue();

            // Flatten the submitted cards into one list
            List<Map<String, String>> allCards = groups.stream().flatMap(List::stream).toList();

            // Extract submitted UUIDs
            Set<String> submittedUUIDs = allCards.stream()
                    .map(card -> card.get("uuid"))
                    .collect(Collectors.toSet());

            // Extract original UUIDs
            List<Map<String, String>> originalCardList = originalCards.get(playerId);
            if (originalCardList == null) {
                throw new IllegalArgumentException("No cards found for playerId: " + playerId + " in original card data");
            }

            Set<String> originalUUIDs = originalCardList.stream()
                    .map(card -> card.get("uuid"))
                    .collect(Collectors.toSet());

            // Validate card UUIDs
            if (!originalUUIDs.equals(submittedUUIDs)) {
                throw new IllegalArgumentException("Submitted cards do not match original cards for playerId: " + playerId);
            }

            // Validate card count
            if (allCards.size() < 13 || allCards.size() > 14) {
                validationResult.put(playerId, Map.of(
                        "error", "Invalid number of cards. A player must have 13 cards.",
                        "totalCards", allCards.size()
                ));
                continue;
            }

            // Classification logic remains unchanged below...
            Map<String, Object> playerResult = new HashMap<>();
            List<List<Map<String, String>>> pureSequences = new ArrayList<>();
            List<List<Map<String, String>>> sequences = new ArrayList<>();
            List<List<Map<String, String>>> sets = new ArrayList<>();
            List<List<Map<String, String>>> invalid = new ArrayList<>();

            boolean hasPureSequence = false;

            for (List<Map<String, String>> group : groups) {
                List<String> cards = group.stream().map(c -> c.get("card")).toList();
                if (isAValidPureSequence(cards)) {
                    pureSequences.add(group);
                    hasPureSequence = true;
                } else if (isAValidSequence(cards)) {
                    sequences.add(group);
                } else if (isAValidSet(cards)) {
                    sets.add(group);
                } else {
                    invalid.add(group);
                }
            }

            int count = 1;
            if (!hasPureSequence) {
                double totalInvalidPoints = 0;
                for (List<Map<String, String>> group : groups) {
                    List<String> cards = group.stream().map(c -> c.get("card")).toList();
                    double groupPoints = calculateInvalidCardPoints(cards);
                    totalInvalidPoints += groupPoints;

                    if (isAValidSequence(cards)) {
                        playerResult.put("sequence" + count++, Map.of("cards", group, "invalid point", groupPoints));
                    } else if (isAValidSet(cards)) {
                        playerResult.put("set" + count++, Map.of("cards", group, "invalid point", groupPoints));
                    } else {
                        playerResult.put("invalid" + count++, Map.of("cards", group, "invalid point", groupPoints));
                    }
                }
                playerResult.put("totalInvalidPoints", totalInvalidPoints);
            } else {
                int pureSeqCount = 1, seqCount = 1, setCount = 1;

                for (List<Map<String, String>> pureSeq : pureSequences) {
                    playerResult.put("pureSequence" + (pureSequences.size() > 1 ? pureSeqCount++ : ""),
                            Map.of("cards", pureSeq, "invalid point", 0));
                }

                for (List<Map<String, String>> seq : sequences) {
                    List<String> cardStrs = seq.stream().map(c -> c.get("card")).toList();
                    playerResult.put("sequence" + (sequences.size() > 1 ? seqCount++ : ""),
                            Map.of("cards", seq, "invalid point", 0));
                }

                for (List<Map<String, String>> set : sets) {
                    List<String> cardStrs = set.stream().map(c -> c.get("card")).toList();
                    playerResult.put("set" + (sets.size() > 1 ? setCount++ : ""),
                            Map.of("cards", set, "invalid point", 0));
                }

                for (List<Map<String, String>> inv : invalid) {
                    List<String> cardStrs = inv.stream().map(c -> c.get("card")).toList();
                    playerResult.put("invalid" + count++, Map.of("cards", inv, "invalid point", calculateInvalidCardPoints(cardStrs)));
                }
            }

            validationResult.put(playerId, playerResult);
        }

        return validationResult;
    }

    private boolean isAValidPureSequence(List<String> cards) {
        if (cards.size() < 3) return false;
        String suit = getSuit(cards.get(0));
        int prevRank = getCardRank(cards.get(0));

        for (int i = 1; i < cards.size(); i++) {
            if (isJoker(cards.get(i))) return false;
            if (!getSuit(cards.get(i)).equals(suit) || getCardRank(cards.get(i)) != prevRank + 1) {
                return false;
            }
            prevRank = getCardRank(cards.get(i));
        }
        return true;
    }

    private boolean isAValidSequence(List<String> cards) {
        if (cards.size() < 3) return false;

        String suit = null;
        int prevRank = -1;
        boolean hasJoker = false;

        for (String card : cards) {
            if (isJoker(card)) {
                hasJoker = true;
                continue;
            }

            String currentSuit = getSuit(card);
            int currentRank = getCardRank(card);

            // The first valid card sets the suit
            if (suit == null) {
                suit = currentSuit;
                prevRank = currentRank;
                continue;
            }

            // Suit mismatch means it's not a valid sequence
            if (!currentSuit.equals(suit)) {
                return false;
            }

            // Check rank continuity
            if (currentRank != prevRank + 1 && !hasJoker) {
                return false;
            }

            prevRank = currentRank;
            hasJoker = false;
        }
        return true;
    }

    private boolean isAValidSet(List<String> cards) {
        if (cards.size() < 3 || cards.size() > 4) return false;

        String rank = null;
        Set<String> suits = new HashSet<>();
        int jokerCount = 0;

        for (String card : cards) {
            if (isJoker(card)) {
                jokerCount++;
                continue;
            }

            String currentRank = getRank(card);
            if (rank == null) {
                rank = currentRank;
            } else if (!rank.equals(currentRank)) {
                return false;
            }

            suits.add(getSuit(card));
        }

        // A valid set requires same rank, all suits different (max 4), jokers allowed
        return suits.size() + jokerCount == cards.size();
    }


    private double validatePureSequence(List<String> pureSequence) {
        double lostPoints = 0;
        if (pureSequence != null && !pureSequence.isEmpty()) {
            if (!isValidPureSequence(pureSequence)) {
                lostPoints += calculateInvalidCardPoints(pureSequence);
            }
        }
        return lostPoints;
    }

    private double validateSequence(List<String> sequence) {
        double lostPoints = 0;
        if (sequence != null && !sequence.isEmpty()) {
            if (!isValidSequence(sequence)) {
                lostPoints += calculateInvalidCardPoints(sequence);
            }
        }
        return lostPoints;
    }

    private double validateSet(List<String> set) {
        double lostPoints = 0;
        if (set != null && !set.isEmpty()) {
            if (!isValidSet(set)) {
                lostPoints += calculateInvalidCardPoints(set);
            }
        }
        return lostPoints;
    }

    // Helper method to check if a card is a joker
    private boolean isJoker(String card) {
        return card.contains("Joker");
    }

    // Update the pure sequence validation to handle jokers
    private boolean isValidPureSequence(List<String> cards) {
        if (cards.size() < 3) return false;

        // Get the suit and rank of the first non-joker card
        String suit = null;
        int prevRank = -1;
        for (String card : cards) {
            if (!isJoker(card)) {
                suit = getSuit(card);
                prevRank = getCardRank(card);
                break;
            }
        }

        if (suit == null || prevRank == -1) {
            // If all cards are jokers, it cannot be a valid pure sequence
            return false;
        }

        for (int i = 1; i < cards.size(); i++) {
            String currentCard = cards.get(i);
            if (isJoker(currentCard)) {
                prevRank++; // Jokers can replace missing cards in the sequence
                continue;
            }

            String currentSuit = getSuit(currentCard);
            int currentRank = getCardRank(currentCard);

            // Check if all non-joker cards are of the same suit and in consecutive rank order
            if (!currentSuit.equals(suit) || currentRank != prevRank + 1) {
                return false;
            }

            prevRank = currentRank;
        }

        return true;
    }

    // Update the sequence validation to handle jokers
    private boolean isValidSequence(List<String> cards) {
        if (cards.size() < 3) return false;

        // Sort cards based on rank (Jokers go at the end to maintain order)
        cards.sort((a, b) -> {
            if (isJoker(a)) return 1;
            if (isJoker(b)) return -1;
            return Integer.compare(getCardRank(a), getCardRank(b));
        });

        String suit = null;
        int prevRank = -1;
        int jokerCount = 0;

        for (String card : cards) {
            if (isJoker(card)) {
                jokerCount++;
                continue;
            }

            int currentRank = getCardRank(card);
            if (suit == null) {
                suit = getSuit(card);
            } else if (!getSuit(card).equals(suit)) {
                return false; // Sequence must have the same suit
            }

            if (prevRank != -1) {
                int gap = currentRank - prevRank;
                if (gap > 1) {
                    if (gap - 1 > jokerCount) {
                        return false; // Not enough jokers to fill the gap
                    }
                    jokerCount -= (gap - 1);
                }
            }

            prevRank = currentRank;
        }
        return true;
    }

    // Update the set validation to handle jokers
    private boolean isValidSet(List<String> cards) {
        if (cards.size() < 3 || cards.size() > 4) return false;

        String rank = null;
        Set<String> suits = new HashSet<>();
        int jokerCount = 0;

        for (String card : cards) {
            if (isJoker(card)) {
                jokerCount++;
                continue;
            }

            if (rank == null) {
                rank = getRank(card);
            } else if (!rank.equals(getRank(card))) {
                return false;
            }

            suits.add(getSuit(card));
        }

        return suits.size() + jokerCount == cards.size();
    }

    private double calculateInvalidCardPoints(List<String> invalidCards) {
        double totalPoints = 0;
        for (String card : invalidCards) {
            totalPoints += getCardPoints(card);
        }
        return totalPoints;
    }

    private int getCardRank(String card) {
        String rank = card.split(" ")[0];
        return switch (rank) {
            case "A" -> 1;
            case "J" -> 11;
            case "Q" -> 12;
            case "K" -> 13;
            default -> Integer.parseInt(rank);
        };
    }

    private String getRank(String card) {
        return card.split(" ")[0];
    }

    private String getSuit(String card) {
        return card.split(" ")[2];
    }

    private double getCardPoints(String card) {
        String rank = getRank(card);
        return switch (rank) {
            case "J", "Q", "K", "A" -> 10;
            default -> Integer.parseInt(rank);
        };
    }

}
