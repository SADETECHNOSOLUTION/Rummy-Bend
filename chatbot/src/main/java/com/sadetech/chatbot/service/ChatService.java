package com.sadetech.chatbot.service;

import com.sadetech.chatbot.model.ChatRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatService {

    private final RestTemplate restTemplate;
    private static final String RASA_URL = "http://localhost:5005/webhooks/rest/webhook";

    public ChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> sendMessageToRasa(ChatRequest chatRequest) {
        Map<String, String> payload = new HashMap<>();
        payload.put("sender", chatRequest.getSender());  // Player ID
        payload.put("message", chatRequest.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        return restTemplate.exchange(RASA_URL, HttpMethod.POST, entity, String.class);
    }

}
