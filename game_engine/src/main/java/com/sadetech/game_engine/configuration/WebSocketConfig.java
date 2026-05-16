package com.sadetech.game_engine.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // this is your endpoint
                .setAllowedOriginPatterns("*") // allow all for now
                .withSockJS(); // support for older browsers
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // To support native
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app"); // for messages sent to server
        registry.enableSimpleBroker("/topic"); // for messages sent to client
    }
}
