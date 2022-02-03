package com.pirimid.cryptotrade.websocket.gemini.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
"fill" : {
    "trade_id" : "652166",
    "liquidity" : "Taker",
    "price" : "714.00",
    "amount" : "2",
    "fee" : "3.57",
    "fee_currency" : "USD"
  },
 */

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
