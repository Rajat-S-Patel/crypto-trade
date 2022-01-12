package com.pirimid.cryptotrade.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;

public enum OrderType {
    MARKET("market"),
    LIMIT("limit");
    String value;
    @JsonValue
    public String getValue(){
        return  this.value;
    }
    OrderType(String value){this.value = value;}
}
