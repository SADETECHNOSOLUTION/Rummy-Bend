package com.sadetech.friend_request.exception;

public class RequestNotFoundException extends RuntimeException{
    public RequestNotFoundException(String message){
        super(message);
    }
}
