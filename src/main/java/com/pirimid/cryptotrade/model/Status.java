package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
    NEW("new"),
    PARTIALLY_FILLED("partially_filled"),
    FILLED("filled"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    EXPIRED("expired"),
    OPEN("open"),
    PENDING("pending");
    String value;
    @JsonValue
    public String getValue(){
        return  this.value;
    }
    Status(String value){this.value = value;}
}
