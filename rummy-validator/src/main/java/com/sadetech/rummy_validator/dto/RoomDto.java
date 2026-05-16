package com.sadetech.rummy_validator.dto;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
public class RoomDto {

    private String roomId;
    private int roomSize;
    private String roomType; //Pool,Deal,Point
    private String gameMode; //Practice,Cash
    private String gameStatus; //Waiting for player,Match making, Started, On going, Finished
    private int issuedPoint;
    private int playerCount;
    private int totalRounds;
    private String playerId1;
    private String playerId2;
    private String playerId3;
    private String playerId4;
    private String playerId5;
    private String playerId6;
    private String playerId7;
    private String playerId8;
    private String playerId9;
    private String matchWinner;
    private String  entryType; //Chips or Money
    private double entryPrice; //Chips or Money deduced for a single player
    private double totalPrice; //Total chips or money deduced from players in a room and given to winner
    private LocalDateTime roomCreatedAt;
    private String tournamentId;
}
