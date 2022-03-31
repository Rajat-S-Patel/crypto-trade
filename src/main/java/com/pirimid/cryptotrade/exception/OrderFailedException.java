package com.pirimid.cryptotrade.exception;

public class OrderFailedException extends RuntimeException{
    public OrderFailedException(String message){
        super(message);
    }
}
