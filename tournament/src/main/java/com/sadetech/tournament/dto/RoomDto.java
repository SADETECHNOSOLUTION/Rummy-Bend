package com.sadetech.tournament.dto;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoomDto {

    private String roomId;
    private int roomSize;
    private String roomType; //Pool,Deal,Point
    private String gameMode; //Practice,Cash
    private String gameStatus; //Waiting for player,Match making, Started, On going, Finished
    private int issuedPoint;
    private int playerCount;
    private List<PlayerDetails> playerDetails;
    private String matchWinner;
    private List<String> playerRank;
    private String  entryType; //Chips or Money
    private double entryPrice; //Chips or Money deduced for a single player
    private double totalPrice; //Total chips or money deduced from players in a room and given to winner
    private LocalDateTime roomCreatedAt;
    private String tournamentId;

}
