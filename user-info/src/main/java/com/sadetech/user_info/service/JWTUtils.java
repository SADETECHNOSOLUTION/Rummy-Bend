package com.sadetech.user_info.service;

import com.sadetech.user_info.exception.InvalidTokenException;
import com.sadetech.user_info.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JWTUtils {

    private final SecretKey key;
    private static final long EXPIRATION_TIME = 86_400_000;  // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 604_800_000; // 7 days

    // Read secret key from application.yml
    public JWTUtils(@Value("${jwt.secret}") String secretString) {
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String generateToken(User userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer("sadetech-rummy")
                .claim("roles", userDetails.getRole())
                .claim("playerId", userDetails.getPlayerId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(HashMap<String, Object> claims, User userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public String extractPlayerId(String token) {
        if (isTokenExpired(token)) {
            throw new InvalidTokenException("Token is expired.");
        }

        return extractClaims(token, claims -> claims.get("playerId")).toString();
    }

}
