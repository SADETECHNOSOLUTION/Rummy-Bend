package com.sadetech.rummy_validator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private String rank;
    private String suit;
    private boolean isJoker;

    public static Card fromString(String cardStr) {
        if (cardStr == null || cardStr.trim().isEmpty()) {
            return null;
        }

        String[] parts = cardStr.split(" of ");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid card format: " + cardStr);
        }

        String rank = parts[0];
        String suit = parts[1];
        boolean isJoker = suit.contains("Joker");

        return new Card(rank, suit, isJoker);
    }

    public int getPointValue() {
        if (isJoker) return 0;

        switch (rank.toUpperCase()) {
            case "A": return 1;
            case "K":
            case "Q":
            case "J": return 10;
            default:
                try {
                    return Integer.parseInt(rank);
                } catch (NumberFormatException e) {
                    return 10; // Default for special cards
                }
        }
    }
}
