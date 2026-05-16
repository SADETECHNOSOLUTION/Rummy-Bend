package com.sadetech.room_creation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "game_room")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    private String roomId;
    private int roomSize;
    private String roomType; //Pool,Deal,Point
    private String gameMode; //Practice,Cash
    private String gameStatus; //Waiting for player,Match making, Started, On going, Finished, Exited
    private int issuedPoint;
    private int playerCount;
    private int totalRounds;
    private List<PlayerDetails> playerDetails = new ArrayList<>();;
    private String matchWinner;
    private String  entryType; //Chips or Money
    private double entryPrice; //Chips or Money deduced for a single player
    private double totalPrice; //Total chips or money deduced from players in a room and given to winner
    private int currentRound = 1;
    private String currentTurn;
    @CreatedDate
    private LocalDateTime roomCreatedAt;
    private String tournamentId;
    private String visibility;  //Public or Private
    private double pointValue; // Point value - rupees / point = pointValue
    private List<String> lastGame = new ArrayList<>();
    private List<String> isNotActive = new ArrayList<>();
    private List<String> exitPlayer = new ArrayList<>();

}
