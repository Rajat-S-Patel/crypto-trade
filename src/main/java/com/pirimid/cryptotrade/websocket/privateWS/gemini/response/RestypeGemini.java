package com.pirimid.cryptotrade.websocket.privateWS.gemini.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RestypeGemini {
    INITIAL("initial"),
    BOOKED("booked"),
    ACCEPTED("accepted"),
    FILL("fill"),
    CLOSED("closed"),
    CANCELLED("cancelled"),
    REJECTED("rejected");

    String value;

    RestypeGemini(String v) {
        value = v;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
