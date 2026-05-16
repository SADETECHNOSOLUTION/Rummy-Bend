package com.sadetech.user_info.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Settings {
    @Id
    private String id;
    private String playerId;
    private boolean sound;
    private boolean vibration;
    private boolean notification;
    private boolean autoShuffleCards;
    private boolean location;
    private boolean calendar;
}
