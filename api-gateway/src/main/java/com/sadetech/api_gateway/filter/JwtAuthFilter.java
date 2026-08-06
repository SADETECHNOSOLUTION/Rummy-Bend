package com.sadetech.api_gateway.filter;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/user/register",
            "/api/user/login",
            "/api/user/verifyOtp",
            "/api/user/otp",
            "/api/user/forgot-password",
            "/api/user/reset-password",
            "/api/user/uploads/**",
            "/ws/player-status",
            "/api/user/send-otp-mobile",
            "/api/user/login/otp",
            "/api/user/get-all-user",
            "/api/user/generate-referral-link",
            "/api/user/verify-otp-register",
            "/api/user/verify-otp",
            "/api/user/register-mobile",
            "/api/user/verify-otp-register-with-referral",
            "/api/cards/points",
            "/api/cards/get-points"
    );

    private boolean isExcludedPath(String path) {
        for (String excludedPath : EXCLUDED_PATHS) {
            if (excludedPath.endsWith("/**")) {
                String prefix = excludedPath.substring(0, excludedPath.length() - 3);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else {
                if (path.equals(excludedPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Value("${jwt.secret}")
    String secretKey;

    public JwtAuthFilter() {
        super(Config.class);
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            // Bypass filter for excluded paths
            if (isExcludedPath(path)) {
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();
            String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            // Check if the "Authorization" header exists and starts with "Bearer "
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Extract token from header
            String token = authHeader.substring(7);

            // Validate the token
            if (!isTokenValid(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String playerId = extractPlayerId(token);

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header("x-player-id", playerId)
                    .build();

            // If valid, proceed with the request
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private boolean isTokenValid(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            return !claims.getExpiration().before(new java.util.Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String extractPlayerId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(token)
                .getBody();

        return claims.get("playerId").toString();
    }

    public static class Config {
    }
}