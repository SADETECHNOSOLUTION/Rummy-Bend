package com.sadetech.user_info.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleAuthService {

    private static final String GOOGLE_CLIENT_ID = "925660865360-ebc0917dqkst92gf304c8lrvufhhb5fb.apps.googleusercontent.com"; // Fixed

    public String verifyGoogleToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            )
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if (googleIdToken == null) {
                throw new IllegalArgumentException("Invalid ID Token");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            if (payload.getEmailVerified() == null || !payload.getEmailVerified()) {
                throw new IllegalArgumentException("Email not verified by Google");
            }

            return payload.getEmail(); // ✅ Extract email from token

        } catch (Exception e) {
            throw new IllegalArgumentException("Google ID Token verification failed: " + e.getMessage());
        }
    }
}
