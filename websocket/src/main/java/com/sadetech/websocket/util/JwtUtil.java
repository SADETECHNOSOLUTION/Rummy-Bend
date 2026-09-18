package com.sadetech.websocket.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

public class JwtUtil {

    private static final SecretKey KEY;
    private static final Logger logger = Logger.getLogger(JwtUtil.class.getName());

    static {
        String secretString = "mySuperSecretKeyForLocalDevelopment1234567890";
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        KEY = new SecretKeySpec(keyBytes, "HmacSHA256"); // Correct key creation
    }

    public static Claims validateToken(String token) {
        try {
            logger.info("Attempting to validate token...");

            Claims claims = Jwts.parser()
                    .verifyWith(KEY)  // Ensure you're using the correct key for validation
                    .build()
                    .parseSignedClaims(token)  // Use parseClaimsJws for validation
                    .getPayload();  // Extract the claims
            logger.info("Token validated successfully: " + claims);
            return claims;
        } catch (Exception e) {
            logger.severe("Token validation failed: " + e.getMessage());
            throw new RuntimeException("Invalid token", e);  // Propagate exception after logging
        }
    }


    public static boolean isTokenValid(String token) {
        try {
            Claims claims = validateToken(token);
            Date expiration = claims.getExpiration();
            logger.info("Token expiration: " + expiration);
            logger.info("Current time: " + new Date());
            return expiration.after(new Date());
        } catch (Exception e) {
            logger.severe("Token is invalid: " + e.getMessage());
            return false;
        }
    }
}
