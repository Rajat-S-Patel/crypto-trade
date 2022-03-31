package com.pirimid.cryptotrade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private Map<String,Object> getMessageBody(String message){
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("message",message);
        return  body;
    }

    @ExceptionHandler(OrderFailedException.class)
    public ResponseEntity<Object> handleOrderFailedException(OrderFailedException e, WebRequest request){
        Map<String,Object> body = getMessageBody(e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> handleAccountNotFoundException(AccountNotFoundException e, WebRequest request){
        Map<String,Object> body = getMessageBody(e.getMessage());
        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExchangeNotFoundException.class)
    public ResponseEntity<Object> handleExchangeNotFoundException(ExchangeNotFoundException e, WebRequest request){
        Map<String,Object> body = getMessageBody(e.getMessage());
        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PairException.class)
    public ResponseEntity<Object> handlePairException(PairException e, WebRequest request){
        Map<String,Object> body = getMessageBody(e.getMessage());
        return new ResponseEntity<>(body,HttpStatus.BAD_REQUEST);
    }
}
