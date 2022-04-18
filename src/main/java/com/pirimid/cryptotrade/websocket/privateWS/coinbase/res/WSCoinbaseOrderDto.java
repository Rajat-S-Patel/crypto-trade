package com.pirimid.cryptotrade.websocket.privateWS.coinbase.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WSCoinbaseOrderDto {
    private String type;
    @JsonProperty("client_oid")
    private String clientOid;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("profile_id")
    private UUID profileId;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("order_type")
    private String orderType;
    private Double size;
    private String side;
    @JsonProperty("product_id")
    private String productId;
    private Date time;
    private Double price;
    private Double funds;
    private String reason;
    @JsonProperty("remaining_size")
    private Double remainingSize;
}
