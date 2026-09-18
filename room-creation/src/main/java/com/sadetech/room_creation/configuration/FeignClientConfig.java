package com.sadetech.room_creation.configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();

                    // Forward the Authorization token
                    String authorizationHeader = request.getHeader("Authorization");
                    if (authorizationHeader != null) {
                        template.header("Authorization", authorizationHeader);
                    }

                    // Forward the x-player-id header as well just in case
                    String playerIdHeader = request.getHeader("x-player-id");
                    if (playerIdHeader != null) {
                        template.header("x-player-id", playerIdHeader);
                    }
                }
            }
        };
    }
}