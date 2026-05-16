package com.sadetech.chatbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "chatbot_rasa")
public class ChatRequest {
    private String sender;  // Player ID
    private String message;
}
