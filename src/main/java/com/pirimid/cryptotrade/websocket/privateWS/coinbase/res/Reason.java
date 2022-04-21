package com.pirimid.cryptotrade.websocket.privateWS.coinbase.res;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Reason {
    FILLED("filled"),
    CANCELED("canceled");
    String value;

    Reason(String v) {
        value = v;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
