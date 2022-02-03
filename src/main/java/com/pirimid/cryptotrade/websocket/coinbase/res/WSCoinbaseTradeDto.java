package com.pirimid.cryptotrade.websocket.coinbase.res;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WSCoinbaseTradeDto {
    private String type;
    @JsonProperty("trade_id")
    private String tradeId;
    @JsonProperty("taker_order_id")
    private String takerOrderId;
    @JsonProperty("maker_order_id")
    private String makerOrderId;
    @JsonProperty("taker_profile_id")
    private String takerProfileId;
    @JsonProperty("taker_user_id")
    private String takerUserId;
    @JsonProperty("taker_fee_rate")
    private Double takerFeeRate;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("profile_id")
    private String profileId;
    private Double size;
    private String side;
    @JsonProperty("product_id")
    private String productId;
    private Date time;
    private Double price;
    private Double funds;
    @JsonProperty("remaining_size")
    private Double remainingSize;

}
