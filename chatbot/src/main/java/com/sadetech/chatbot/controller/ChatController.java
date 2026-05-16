package com.sadetech.chatbot.controller;

import com.sadetech.chatbot.model.ChatRequest;
import com.sadetech.chatbot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatController {

    @Autowired
    private ChatService chatService;


    @PostMapping("/send")
    public ResponseEntity<String> chatWithRasa(@RequestBody ChatRequest chatRequest) {
        return chatService.sendMessageToRasa(chatRequest);
    }
}
