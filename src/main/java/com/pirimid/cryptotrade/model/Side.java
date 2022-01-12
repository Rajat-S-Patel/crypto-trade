package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Side {
    BUY("buy"),
    SELL("sell");
    String value;
    @JsonValue
    public String getValue(){
        return  this.value;
    }
    Side(String value){this.value = value;}
}
