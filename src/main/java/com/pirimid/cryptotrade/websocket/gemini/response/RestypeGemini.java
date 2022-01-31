package com.pirimid.cryptotrade.websocket.gemini.response;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RestypeGemini {
    ACCEPTED("accepted"),
    FILL("fill"),
    CLOSED("closed"),
    REJECTED("rejected"),
    INITIAL("initial");
    String value;

    RestypeGemini(String v) {
        value = v;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}


