package com.sadetech.api_gateway.filter;

import io.jsonwebtoken.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

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
        // Handle dynamic paths by using pattern matching with wildcards
        for (String excludedPath : EXCLUDED_PATHS) {
            if (excludedPath.endsWith("/**")) {
                // For paths like "/api/admin/check-email/**", match everything under that path
                String prefix = excludedPath.substring(0, excludedPath.length() - 3); // Remove "/**"
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else {
                // For exact matches (non-dynamic paths)
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

            ServerHttpRequest modifiedRequest =  exchange.getRequest().mutate()
                    .header("x-player-id",playerId)
                    .build();

            // If valid, proceed with the request
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private boolean isTokenValid(String token) {
        try {
            // Decode the token with Apache Commons Codec
            byte[] decodedKey = Base64.decodeBase64(secretKey);

            // Parse the JWT and validate it
            Claims claims = Jwts.parser()
                    .setSigningKey(decodedKey) // Use the decoded secret key
                    .parseClaimsJws(token)
                    .getBody();

            // Additional checks can be added here (e.g., check claims, roles, etc.)
            return !claims.getExpiration().before(new java.util.Date()); // Ensure token is not expired
        } catch (SignatureException | ExpiredJwtException e) {
            // Token is invalid or expired
            return false;
        } catch (Exception e) {
            // Other exceptions, token may not be valid
            return false;
        }
    }

    private String extractPlayerId(String token) {

        // Decode the token with Apache Commons Codec
        byte[] decodedKey = Base64.decodeBase64(secretKey);

        // Parse the JWT and validate it
        Claims claims = Jwts.parser()
                .setSigningKey(decodedKey) // Use the decoded secret key
                .parseClaimsJws(token)
                .getBody();

        return claims.get("playerId").toString();

    }

    public static class Config {
    }
}
