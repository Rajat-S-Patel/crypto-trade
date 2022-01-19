package com.pirimid.cryptotrade.helper.exchange.gemini.dto.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderResponse {

    @JsonProperty("order_id")
    private Long orderId;

    private Double price;

    @JsonProperty("original_amount")
    private Double originalAmount;

    private String symbol;

    private String side;
    private String type;
    @JsonProperty("timestampms")
    private Long timestamp;

    @JsonProperty("executed_amount")
    private Double executedAmount;

    @JsonProperty("is_live")
    private boolean isLive;

    @JsonProperty("is_cancelled")
    private boolean isCancelled;
}
