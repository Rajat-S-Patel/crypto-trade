package com.pirimid.cryptotrade.websocket.gemini.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderResponse {
    private String type;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("api_session")
    private String apiSession;
    private String symbol;
    private String side;
    @JsonProperty("order_type")
    private String orderType;
    private String timestamp;
    private Date timestampms;
    @JsonProperty("is_live")
    private boolean isLive;
    @JsonProperty("is_cancelled")
    private boolean isCancelled;
    @JsonProperty("is_hidden")
    private boolean isHidden;
    @JsonProperty("original_amount")
    private Double originalAmount;
    @JsonProperty("executed_amount")
    private Double executedAmount;
    private Double price;
    @JsonProperty("socket_sequence")
    private int socketSequence;
    @JsonProperty("total_spend")
    private Double totalSpend;

    private OrderFillResponse fill;
}
