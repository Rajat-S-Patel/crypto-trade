package com.pirimid.cryptotrade.helper.exchange.gemini.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderRequest {

    @JsonProperty("client_order_id")
    private String clientOrderId;
    private long nonce;
    private String request;
    private String symbol;
    private Double amount;
    private Double price;
    private String side;
    private String type;

    @JsonProperty("account")
    private String accountType;
}
