package com.pirimid.cryptotrade.websocket.coinbase.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Typedto {
    private String type;
}
