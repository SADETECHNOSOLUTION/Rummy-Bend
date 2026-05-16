package com.sadetech.missions.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "mission")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mission {

    @Id
    private String id;
    private String playerId;
    private String type;  // Mission or Daily Challenges
    private String heading;
    private String gameType = "Point";
    private String visibility;  // Which player to display
    private String visibilityText;  // Featured ( or ) Only for you
    private String cashType;  // Instant cash
    private String task;   // Win x game to get money
    private String expiryTime;
    private String displayVisibility; // Daily, One time
    private int playerCount; // Like play 9 player match, here 9 is the player count
    @CreatedDate
    private LocalDateTime missionCreatedAt;
    private String remark;  // Remarks you want to give
    private double rewardAmount;
    private double entryAmount;
    private int totalRound;
    private int round;   // Progress of the game, number of wins to achieve the target
    private String progress = "Not yet started";  // Not yet started, In progress, Completed

}