package com.sadetech.api_gateway.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Configuration
public class ApiGatewayConfig {

    @Value("${jwt.secret}")
    private String secretKey;

    private JwtDecoder jwtDecoder;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes(StandardCharsets.UTF_8));
        SecretKey hmacKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(hmacKey).build();
    }

@Bean
public KeyResolver combinedKeyResolver() {
    return exchange -> {
        // 1. Get IP safely
        String ip = "unknown";
        if (exchange.getRequest().getRemoteAddress() != null) {
            ip = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        // 2. Try to get Username, but don't crash if it's not there
        String identifier = "anonymous";
        try {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                identifier = validateTokenAndGetUsername(token); 
            }
        } catch (Exception e) {
            // Log warning: "Could not resolve username for rate limiting, falling back to IP"
            identifier = "unauthenticated";
        }

        // 3. Return the combined key
        return Mono.just(ip + ":" + identifier);
    };
}

    private String extractJwt(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    private String validateTokenAndGetUsername(String token) {
        try {

            if (token == null) {
                return "anonymous";
            }

            Jwt jwt = jwtDecoder.decode(token);

            if (isTokenExpired(jwt)) {
                return "anonymous";
            }

            return jwt.getClaimAsString("sub");
        } catch (Exception e) {
            return "anonymous"; // Return fallback user in case of invalid/expired token
        }
    }

    private boolean isTokenExpired(Jwt jwt) {
        Date expiration = Date.from(Objects.requireNonNull(jwt.getExpiresAt()));
        return expiration.before(new Date());
    }

}
