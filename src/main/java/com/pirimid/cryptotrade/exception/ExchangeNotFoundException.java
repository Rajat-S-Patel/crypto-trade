package com.pirimid.cryptotrade.exception;

public class ExchangeNotFoundException extends RuntimeException{
    public ExchangeNotFoundException(String message){
        super(message);
    }
}
