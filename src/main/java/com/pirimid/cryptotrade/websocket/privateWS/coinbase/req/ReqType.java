package com.pirimid.cryptotrade.websocket.privateWS.coinbase.req;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReqType {
    OPEN("open"),
    RECEIVED("received"),
    DONE("done"),
    MATCH("match"),
    CHANGE("change"),
    ACTIVATE("activate"),
    SUBSCRIBE("subscribe"),
    UNSUBSCRIBE("unsubscribe");
    String value;
    ReqType(String v){
        value = v;
    }
    @JsonValue
    String getValue(){
        return value;
    }
}
