package com.sadetech.user_info.exception;

public class SamePlayerIdException extends RuntimeException {
    public SamePlayerIdException(String message) {
        super(message);
    }
}
