package com.pirimid.cryptotrade.websocket.publicWS.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickerGeminiEvents {
    private String type;
    private String symbol;
    private String reason;
    private double price;
    private double delta;
    private double remaining;
    private String side;
}
