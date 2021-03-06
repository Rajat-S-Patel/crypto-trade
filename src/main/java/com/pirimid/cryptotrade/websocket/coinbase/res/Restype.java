package com.pirimid.cryptotrade.websocket.coinbase.res;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Restype {
    RECEIVED("received"),
    DONE("done"),
    MATCH("match"),
    SUBSCRIPTIONS("subscriptions"),
    OPEN("open");
    String value;

    Restype(String v) {
        value = v;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
