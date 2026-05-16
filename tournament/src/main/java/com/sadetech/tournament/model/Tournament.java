package com.sadetech.tournament.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "tournament")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tournament {

    @Id
    private String id;
    private List<String> playerId = new ArrayList<>();
    private String tournamentName;
    private String tournamentType; //Free,Cash
    private String entryType;
    private double entryFee;
    private List<String> matchDay = new ArrayList<>(); // All day,Weekly - Show 7 days in dropdown ,Occasional,Hourly, ect ...
    private LocalDate matchStartingDate;   // Only date
    private LocalTime matchStartingAt;  //Time
    private String tournamentMode; //Point,Deal
    private String tournamentStatus = "Waiting"; //Waiting, Started, Ended
    private int tournamentRoomSize;
    private String estimatedDuration;
    private List<String> roomId = new ArrayList<>();
    private List<String> selectedPlayers = new ArrayList<>();
    private boolean isLateJoinAvailable;
    private boolean isRejoinAvailable;
    private int totalPlayersEntry;  // 20,000 people can join
    private boolean isMaxPlayerEntryAllowed;  // If yes, allow, if not, don't allow
    private int maxPlayersEntry;  // Maximum 25,000 people included late join, not more than that.
//    private String visibility; // All, Withdrawn player, In game (Money deposited players)
    private String description;
    private String grandTotal;  //Total price distribution amount and ticket to display
    private int totalTicket;     //Total price distribution ticket to display
    private double totalCash;   //Total price distribution amount to display
    private boolean isPrizeTicketAvailable;
    private String prizeTicketName;  // Ticket to be given for winners
    private String entryTicketName;  // Entry ticket name
    private int totalRounds;   // Total number of rounds taken to finsih the tournament
    private int winners;     // Winner count, number of prize distribution = winner
    private int dealPerRound;
    private String textDescription;
    private List<PriceDistribution> priceDistributions;
    private List<DealStructure>dealStructures;
    private boolean rebuy; //Available if true and not available if false.
    private int startingChipStack;  // Starting chips for match, i.e. 800 chips, at starting round
    private double pointForDeal; // Points to be given for each player when match starts.
    private String extraPrizePool;
    private List<PointStructure> pointStructures;
    private List<Rejoin> rejoins;
    private List<LateJoin> lateJoins;
    private String turnTime; // 20 sec for a move
    private String bonusTime;  // 10 sec for a move
    private String gameVariant; // 13 card rummy
    private String dropPoints; // Points reduces on drop, i.e. First:20, Middle:40, Full:80
    private LocalTime showTime; //In which time the tournament need to be displayed.
    private LocalTime regStartTime;
    private LocalTime regEndingTime;
    @CreatedDate
    private LocalDateTime createdAt;

}