package com.sadetech.user_info.exception;

public class MoneyRequestNotFoundException extends RuntimeException{
    public MoneyRequestNotFoundException(String message){
        super(message);
    }
}
