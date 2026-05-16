package com.sadetech.game_engine.service;

import com.sadetech.game_engine.dto.CardDistributionResponse;
import com.sadetech.game_engine.dto.PlayerDetails;
import com.sadetech.game_engine.dto.RankPlayersDto;
import com.sadetech.game_engine.dto.RoomDTO;
import com.sadetech.game_engine.exception.*;
import com.sadetech.game_engine.model.Card;
import com.sadetech.game_engine.model.Room;
import com.sadetech.game_engine.repository.CardRepository;
import com.sadetech.game_engine.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    private RoomRepository roomRepository; // ADD THIS: This fixes the "variable roomRepository" errors

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CardRepository cardRepository;

    // @Autowired
    // private RoomFeignClient roomFeignClient;


    public Card initializeDeckForSixPlayer(String roomId) {
        logger.info("Initializing deck for 6-player room: {}", roomId);

        Map<String, Object> result = createDeckForSixPlayer();
        List<Map<String, String>> deck = (List<Map<String, String>>) result.get("deck");
        String jokerCard = (String) result.get("selectedJoker");

        if (deck == null || deck.isEmpty()) {
            throw new CardGeneratorFailedException("Deck creation failed. No cards generated for room: " + roomId);
        }

        Card card = new Card(null, deck, jokerCard, roomId, new HashMap<>(), deck, new HashMap<>(), new ArrayList<>(), null, new HashMap<>(), new HashMap<>(),new ArrayList<>());

        logger.info("Saving card to database: {}", card);
        Card savedCard = cardRepository.save(card);
        logger.info("Card saved successfully: {}", savedCard);

        return savedCard;
    }

    public Card initializeDeckForNinePlayer(String roomId){
        Map<String,Object> result = createDeckForNinePlayer();
        String jokerCard =(String) result.get("selectedJoker");
        List<Map<String, String>> deck = (List<Map<String, String>>) result.get("deck");

        if (deck == null || deck.isEmpty()) { // Add this check
            throw new CardGeneratorFailedException("Deck creation failed for 9-player room: " + roomId);
        }

        Card card = new Card(null,deck,jokerCard,roomId,null,null,null,null,null,null,null,new ArrayList<>());
        return cardRepository.save(card);
    }

    private Map<String, Object> createDeckForSixPlayer() {
        List<Map<String, String>> deck = new ArrayList<>();
        String[] suits = {"Heart", "Diamond", "Club", "Spade", "Heart", "Diamond", "Club", "Spade"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "Joker"};

        // Select a random rank
        Random random = new Random();
        String selectedRank = ranks[random.nextInt(ranks.length)];
        if(selectedRank.equalsIgnoreCase("Joker")){
            selectedRank = selectedRank.replaceAll("Joker","A");
        }
        System.out.println("Selected Rank: " + selectedRank); // For verification

        List<String> jokerToDisplay = Arrays.asList(selectedRank + " of Heart", selectedRank + " of Diamond", selectedRank + " of Club", selectedRank + " of Spade");
        String selectedJoker = jokerToDisplay.get(random.nextInt(jokerToDisplay.size()));

        for (String suit : suits) {
            for (String rank : ranks) {
                if (rank.equals("Joker")) {
                    continue; // Skip standalone Joker in this loop
                }

                String cardName;
                if (selectedRank.equals("Joker") && rank.equals("A")) {
                    // Special case for "Joker" replacing "A of Suit" with "A of Suit of Joker"
                    cardName = rank + " of " + suit + " of Joker";
                } else if (rank.equals(selectedRank)) {
                    // Add "Rank of Suit of Joker" for the selected rank
                    cardName = rank + " of " + suit + " of Joker";
                } else {
                    // Add normal cards
                    cardName = rank + " of " + suit;
                }

                // Create card with unique UUID
                Map<String, String> card = new HashMap<>();
                card.put("uuid", UUID.randomUUID().toString());
                card.put("card", cardName);
                deck.add(card);
            }
        }

        // Add standalone Jokers
        for (int i = 0; i < 2; i++) {
            Map<String, String> jokerCard = new HashMap<>();
            jokerCard.put("uuid", UUID.randomUUID().toString());
            jokerCard.put("card", "Joker");
            deck.add(jokerCard);
        }

        // Return the deck and selected rank
        Map<String, Object> result = new HashMap<>();
        result.put("deck", deck);
        result.put("selectedRank", selectedRank);
        result.put("selectedJoker",selectedJoker);
        return result;
    }

    private Map<String,Object> createDeckForNinePlayer(){
        List<Map<String, String>> deck = new ArrayList<>();
        String [] suits = {"Heart", "Diamond", "Club", "Spade","Heart", "Diamond", "Club", "Spade","Heart", "Diamond", "Club", "Spade"};
        String [] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A","Joker"};

        // Select a random rank
        Random random = new Random();
        String selectedRank = ranks[random.nextInt(ranks.length)];
        if(selectedRank.equalsIgnoreCase("Joker")){
            selectedRank = selectedRank.replaceAll("Joker","A");
        }
        System.out.println("Selected Rank: " + selectedRank); // For verification

        List<String > jokerToDisplay = Arrays.asList(selectedRank + " of Heart", selectedRank + " of Diamond", selectedRank + " of Club", selectedRank + " of Spade");
        String selectedJoker = jokerToDisplay.get(random.nextInt(jokerToDisplay.size()));

        for (String suit : suits) {
            for (String rank : ranks) {
                if (rank.equals("Joker")) {
                    continue; // Skip standalone Joker in this loop
                }

                String cardName;
                if (selectedRank.equals("Joker") && rank.equals("A")) {
                    // Special case for "Joker" replacing "A of Suit" with "A of Suit of Joker"
                    cardName = rank + " of " + suit + " of Joker";
                } else if (rank.equals(selectedRank)) {
                    // Add "Rank of Suit of Joker" for the selected rank
                    cardName = rank + " of " + suit + " of Joker";
                } else {
                    // Add normal cards
                    cardName = rank + " of " + suit;
                }

                // Create card with unique UUID
                Map<String, String> card = new HashMap<>();
                card.put("uuid", UUID.randomUUID().toString());
                card.put("card", cardName);
                deck.add(card);
            }
        }
        for (int i = 0; i < 3; i++) {
            Map<String, String> jokerCard = new HashMap<>();
            jokerCard.put("uuid", UUID.randomUUID().toString());
            jokerCard.put("card", "Joker");
            deck.add(jokerCard);
        }

        // Return the deck and selected rank
        Map<String, Object> result = new HashMap<>();
        result.put("deck", deck);
        result.put("selectedRank", selectedRank);
        result.put("selectedJoker",selectedJoker);
        return result;
    }

public CardDistributionResponse getCardDetails(String roomId) {
    // 1. Fetch the card document
    Card card = cardRepository.findByRoomId(roomId);
    if (card == null) {
        throw new CardNotFoundException("No deck found for room: " + roomId);
    }

    List<Map<String, String>> deck = new ArrayList<>(card.getRemainingCards());
    Collections.shuffle(deck);

    // 2. REPLACED FEIGN CALL: Query the database directly for Room details
    // Assuming you have access to the Room collection/repository in this service
    Room room = roomRepository.findByRoomId(card.getRoomId())
            .orElseThrow(() -> new RoomNotFoundException("No room found"));

    // Use the Room entity directly instead of RoomDTO
    List<PlayerDetails> players = room.getPlayerDetails(); 
    int playerCount = players.size();
    int cardsPerPlayer = 13;

    // 3. Distribution Logic (Remains the same)
    if (playerCount * cardsPerPlayer > deck.size() - 1) {
        throw new ResourceNotEnoughException("Not enough cards to distribute!");
    }

    Map<String, List<Map<String, String>>> playerCards = new HashMap<>();
    List<Map<String, String>> allDiscardedCards = new ArrayList<>();
    allDiscardedCards.add(deck.remove(0));

    for (PlayerDetails player : players) {
        String playerId = player.getPlayerId();
        List<Map<String, String>> playerDeck = new ArrayList<>(deck.subList(0, cardsPerPlayer));
        deck.subList(0, cardsPerPlayer).clear();
        playerCards.put(playerId, playerDeck);
    }

    // 4. Update and Save
    card.setPlayerCards(playerCards);
    card.setRemainingCards(new ArrayList<>(deck));
    card.setAllDiscardedCards(allDiscardedCards);
    cardRepository.save(card);

    return new CardDistributionResponse(
            card.getId(),
            card.getJokerCard(),
            card.getRoomId(),
            playerCards,
            card.getRemainingCards(),
            new HashMap<>(),
            allDiscardedCards,
            null,
            new HashMap<>(),
            new HashMap<>()
    );
}

public RankPlayersDto rankPlayersAndGetCards(String roomId) {
    // 1. Fetch the Card object by room ID
    Card card = cardRepository.findByRoomId(roomId);

    if (card == null) {
        throw new CardNotFoundException("No card found for the room ID: " + roomId);
    }

    // 2. Get a player cards map and validate
    Map<String, List<Map<String, String>>> playerCards = card.getPlayerCards();
    if (playerCards == null || playerCards.isEmpty()) {
        throw new PlayerCardNotFoundException("No player cards found for the room ID: " + roomId);
    }

    List<Map.Entry<String, String>> playerCardEntries = new ArrayList<>();
    Map<String, String> playerZeroIndexCards = new HashMap<>();

    for (Map.Entry<String, List<Map<String, String>>> entry : playerCards.entrySet()) {
        String playerId = entry.getKey();
        List<Map<String, String>> cards = entry.getValue();

        if (cards != null && !cards.isEmpty()) {
            String firstCard = cards.get(0).get("card"); 
            playerCardEntries.add(Map.entry(playerId, firstCard));
            playerZeroIndexCards.put(playerId, firstCard); 
        }
    }

    // 3. Sort players based on card rank and suit
    playerCardEntries.sort((entry1, entry2) -> {
        int rankComparison = compareCards(entry2.getValue(), entry1.getValue());
        if (rankComparison != 0) {
            return rankComparison;
        }
        return getCardSuit(entry2.getValue()) - getCardSuit(entry1.getValue());
    });

    String firstRankPlayerId = !playerCardEntries.isEmpty() ? playerCardEntries.get(0).getKey() : null;

    // 4. Prepare ranking order
    List<String> ranking = new ArrayList<>();
    String[] ranks = { "First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth" };
    List<String> orderPlayerByRank = new ArrayList<>();
    for (int i = 0; i < playerCardEntries.size(); i++) {
        String rank = i < ranks.length ? ranks[i] : (i + 1) + "th";
        String playerId = playerCardEntries.get(i).getKey();
        ranking.add(rank + ": " + playerId);
        orderPlayerByRank.add(playerId);
    }

    RankPlayersDto result = new RankPlayersDto(ranking, playerZeroIndexCards, orderPlayerByRank);

    // 5. Update the Card document locally
    card.setOrderPlayersByRank(orderPlayerByRank);
    cardRepository.save(card);

    // 6. REPLACED FEIGN CALL: Update the Room entity directly
    Room room = roomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RoomNotFoundException("No room found for ID: " + roomId));
    
    room.setCurrentTurn(firstRankPlayerId); // Set the player who won the toss/rank
    roomRepository.save(room);

    return result;
}
    // Helper method to compare two cards by rank
    private int compareCards(String card1, String card2) {
        return getCardRank(card1) - getCardRank(card2);
    }

    // Helper method to determine the rank of a card
    private int getCardRank(String card) {
        if (card.equalsIgnoreCase("Joker")) {
            return 15; // Assigning the highest rank to Joker
        }

        // Handle two-digit rank (10)
        String rank = card.split(" ")[0]; // Extract the rank part (first part of the card, e.g., "10")
        return switch (rank) {
            case "A" -> 14; // Ace
            case "K" -> 13; // King
            case "Q" -> 12; // Queen
            case "J" -> 11; // Jack
            case "10" -> 10; // Handle "10"
            case "9" -> 9;
            case "8" -> 8;
            case "7" -> 7;
            case "6" -> 6;
            case "5" -> 5;
            case "4" -> 4;
            case "3" -> 3;
            case "2" -> 2;
            default -> throw new IllegalArgumentException("Invalid card rank: " + card);
        };
    }

    // Helper method to determine the suit of a card
    private int getCardSuit(String card) {
        // Extract the suit part (third part of the card, e.g., "Spade")
        String suit = card.split(" ")[2];
        return switch (suit) {
            case "Spade" -> 4; // Highest suit
            case "Heart" -> 3;
            case "Diamond" -> 2;
            case "Club" -> 1; // Lowest suit
            default -> throw new IllegalArgumentException("Invalid card suit: " + card);
        };
    }

public Card getCardDetailsByRoomId(String roomId) {
    if (roomId == null || roomId.isBlank()) {
        throw new RoomNotFoundException("Room id should not be empty.");
    }

    // REPLACED FEIGN CALL: Check the database directly to see if the room exists
    if (!roomRepository.existsByRoomId(roomId)) {
        throw new RoomNotFoundException("No room found with ID: " + roomId);
    }

    return cardRepository.findByRoomId(roomId);
}

    public Map<String, Map<String, List<Map<String, String>>>> groupCardForPlayersUsingSuit(String roomId) {
        Card card = cardRepository.findByRoomId(roomId);

        if (card == null) {
            logger.info("No room found for the room id {}", roomId);
            throw new CardNotFoundException("No card found for the room id");
        }

        logger.info("Card found for the room id {}", roomId);

        // Get the player cards map
        Map<String, List<Map<String, String>>> playerCards = card.getPlayerCards();

        if (playerCards == null || playerCards.isEmpty()) {
            logger.info("No player cards found for the room id {}", roomId);
            throw new PlayerCardNotFoundException("No player cards available");
        }

        // Map to hold the grouped cards by suit for each player
        Map<String, Map<String, List<Map<String, String>>>> groupedCardsBySuit = new HashMap<>();

        // Iterate through each player's cards
        for (Map.Entry<String, List<Map<String, String>>> entry : playerCards.entrySet()) {
            String playerId = entry.getKey();
            List<Map<String, String>> cards = entry.getValue();

            // Group cards by suit for the current player
            Map<String, List<Map<String, String>>> suitGroupedCards = new HashMap<>();
            for (Map<String, String> cardMap : cards) {
                String cardString = cardMap.get("card"); // Access the "card" value
                String suit = getSuitFromCard(cardString);

                // Add the card to the appropriate suit group
                suitGroupedCards
                        .computeIfAbsent(suit, k -> new ArrayList<>())
                        .add(cardMap); // Add the entire map to preserve uuid and card
            }

            // Add the suit-grouped cards for the current player
            groupedCardsBySuit.put(playerId, suitGroupedCards);
        }

        // Set the grouped cards in the Card object
        card.setGroupedCardsBySuit(groupedCardsBySuit);

        // Save the updated card object back to the database
        cardRepository.save(card);

        logger.info("Grouped cards by suit for players: {}", groupedCardsBySuit);

        return card.getGroupedCardsBySuit(); // Return the updated card object
    }

    // Helper method to extract suit from card string
    private String getSuitFromCard(String card) {
        String[] cardParts = card.split(" of ");
        if (cardParts.length == 2) {
            return cardParts[1]; // Return the suit
        }
        return "Unknown"; // Handle cases where the format is unexpected
    }

public Card updatePlayerCardOnHisTurn(String roomId, String playerId, Boolean pickFromDiscarded, String cardToDiscard, String otherPlayerId, String extractedId) {
    logger.info("Starting updatePlayerCardOnHisTurn for roomId: {}, playerId: {}", roomId, playerId);

    // 1. Fetch current card and room state directly from DB
    Card existingCard = cardRepository.findByRoomId(roomId);
    
    // REPLACED FEIGN: Direct DB lookup for room status/turn validation
    Room room = roomRepository.findByRoomId(roomId)
            .orElseThrow(() -> new RoomNotFoundException("No room found"));

    // 2. Security and State Validations
    if(!playerId.equals(extractedId)){
        throw new UnAuthorizedAccessException("Access denied");
    }

    if(!"Ongoing".equalsIgnoreCase(room.getGameStatus())) {
        throw new IllegalArgumentException("Can't update, the room is not in Ongoing status");
    }

    if(!playerId.equals(room.getCurrentTurn())){
        throw new IllegalArgumentException("Can't update, the current turn is not for the player");
    }

    // 3. Card Deck Management (Reset deck if empty)
    List<Map<String, String>> remainingCards = existingCard.getRemainingCards();
    List<Map<String, String>> allDiscardedCards = existingCard.getAllDiscardedCards();

    if (allDiscardedCards == null) {
        allDiscardedCards = new ArrayList<>();
        existingCard.setAllDiscardedCards(allDiscardedCards);
    }

    if (remainingCards == null || remainingCards.isEmpty()) {
        if (allDiscardedCards.isEmpty()) {
            throw new CardNotFoundException("No cards available in the game to reset the deck.");
        }
        remainingCards = new ArrayList<>(allDiscardedCards);
        Collections.shuffle(remainingCards);
        allDiscardedCards.clear();
        existingCard.setRemainingCards(remainingCards);
    }

    // 4. Step and Hand Initialization
    Map<String, Integer> playerSteps = existingCard.getPlayerSteps();
    if (playerSteps == null) {
        playerSteps = new HashMap<>();
        existingCard.setPlayerSteps(playerSteps);
    }
    playerSteps.put(playerId, playerSteps.getOrDefault(playerId, 0) + 1);

    Map<String, List<Map<String, String>>> playerCards = existingCard.getPlayerCards();
    Map<String, List<Map<String, String>>> discardedCards = existingCard.getDiscardedCards();

    if (discardedCards == null) {
        discardedCards = new HashMap<>();
        existingCard.setDiscardedCards(discardedCards);
    }

    // 5. Card Picking Logic
    Map<String, String> pickedCard = null;
    if (Boolean.TRUE.equals(pickFromDiscarded)) {
        if (allDiscardedCards.isEmpty()) {
            throw new CardNotFoundException("No cards available in the general discard pile.");
        }
        pickedCard = allDiscardedCards.remove(allDiscardedCards.size() - 1);
    } else if (Boolean.FALSE.equals(pickFromDiscarded)) {
        pickedCard = remainingCards.remove(remainingCards.size() - 1);
    }

    if (pickedCard != null) {
        playerCards.get(playerId).add(pickedCard);
    }

    // 6. Discard Logic (using UUID)
    if (cardToDiscard != null && !cardToDiscard.isEmpty()) {
        List<Map<String, String>> playerHand = playerCards.get(playerId);
        Map<String, String> cardToDiscardMap = playerHand.stream()
                .filter(c -> c != null && cardToDiscard.equals(c.get("uuid")))
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException("Card to discard not found in player's hand."));

        playerHand.remove(cardToDiscardMap);
        discardedCards.computeIfAbsent(playerId, k -> new ArrayList<>()).add(cardToDiscardMap);
        allDiscardedCards.add(cardToDiscardMap);
    }

    // 7. Turn Management Logic
    List<String> playerRanking = existingCard.getOrderPlayersByRank();
    if(pickFromDiscarded == null){ // Handling turn skip or specific turn-end logic
        if (playerRanking != null && playerRanking.contains(playerId)) {
            int nextIndex = (playerRanking.indexOf(playerId) + 1) % playerRanking.size();
            String changeTurn = playerRanking.get(nextIndex);
            
            // REPLACED FEIGN: Update Room currentTurn directly in DB
            room.setCurrentTurn(changeTurn);
            roomRepository.save(room);
        }
    }

    // 8. Final Save and Broadcast
    cardRepository.save(existingCard);
    messagingTemplate.convertAndSend("/topic/game/" + roomId, existingCard);
    
    return existingCard;
}

    public Map<String, Object> getGroupedCardsByRoomAndPlayerCustomFormat(String roomId, String playerId) {
        Optional<Card> cardOpt = Optional.ofNullable(cardRepository.findByRoomId(roomId));

        if (cardOpt.isEmpty()) {
            throw new NoSuchElementException("Room ID not found: " + roomId);
        }

        Card card = cardOpt.get();
        Map<String, Map<String, List<Map<String, String>>>> groupedCardsBySuit = card.getGroupedCardsBySuit();

        if (groupedCardsBySuit == null || !groupedCardsBySuit.containsKey(playerId)) {
            throw new NoSuchElementException("Player ID not found in grouped cards: " + playerId);
        }

        Map<String, List<Map<String, String>>> playerCardsBySuit = groupedCardsBySuit.get(playerId);
        List<List<Map<String, String>>> groupedCards = new ArrayList<>(playerCardsBySuit.values());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put(playerId, groupedCards);

        return response;
    }
}