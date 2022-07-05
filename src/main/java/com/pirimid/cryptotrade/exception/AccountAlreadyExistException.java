package com.pirimid.cryptotrade.exception;

public class AccountAlreadyExistException extends RuntimeException{
    public AccountAlreadyExistException(String message){
        super(message);
    }
}
