package com.sadetech.rummy_validator.exception;

public class CardDetailsNotFoundException extends RuntimeException {
    public CardDetailsNotFoundException(String message) {
        super(message);
    }
}
