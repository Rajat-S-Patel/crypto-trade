package com.pirimid.cryptotrade.websocket.privateWS.gemini.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderFillResponse {
    @JsonProperty("trade_id")
    private String tradeId;
    private String liquidity;
    private double price;
    private double amount;
    private double fee;
    @JsonProperty("fee_currency")
    private String feeCurreny;
}
