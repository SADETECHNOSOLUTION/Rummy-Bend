package com.sadetech.user_info.exception;

public class InsufficientMoneyException extends RuntimeException{
    public InsufficientMoneyException(String message){
        super(message);
    }
}
