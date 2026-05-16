package com.sadetech.game_engine.exception;

public class PlayerCardNotFoundException extends RuntimeException{
    public PlayerCardNotFoundException(String message){
        super(message);
    }
}
