package com.sadetech.websocket.configuration;

import com.sadetech.websocket.service.PlayerStatusHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker // 👈 Enables STOMP broker support
@EnableScheduling
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private final MongoTemplate mongoTemplate;

    public WebSocketConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public PlayerStatusHandler playerStatusHandler() {
        return new PlayerStatusHandler(mongoTemplate);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(playerStatusHandler(), "/ws/player-status").setAllowedOrigins("*");
    }

    // 🚀 ADD THIS: Configures the STOMP message broker paths for CardService
    @Override
    public void configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Native WebSocket endpoint for STOMP clients
    }
}