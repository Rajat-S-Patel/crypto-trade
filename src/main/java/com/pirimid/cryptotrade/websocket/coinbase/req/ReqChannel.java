package com.pirimid.cryptotrade.websocket.coinbase.req;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReqChannel {
    HEARTBEAT("heartbeat"),
    STATUS("status"),
    TICKER("ticker"),
    LEVEL2("level2"),
    USER("user"),
    MATCHES("matches"),
    FULL("full");
    String value;
    ReqChannel(String v){
        value=v;
    }
    @JsonValue
    public String getValue(){
        return value;
    }
}
