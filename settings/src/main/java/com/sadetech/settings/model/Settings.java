package com.sadetech.settings.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Settings")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
