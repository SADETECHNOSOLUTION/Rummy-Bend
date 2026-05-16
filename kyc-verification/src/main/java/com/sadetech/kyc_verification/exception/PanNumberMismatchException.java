package com.sadetech.kyc_verification.exception;

public class PanNumberMismatchException extends RuntimeException {
    public PanNumberMismatchException(String message) {
        super(message);
    }
}