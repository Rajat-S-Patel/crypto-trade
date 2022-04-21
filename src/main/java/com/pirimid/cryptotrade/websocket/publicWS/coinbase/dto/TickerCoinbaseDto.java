package com.pirimid.cryptotrade.websocket.publicWS.coinbase.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pirimid.cryptotrade.model.Side;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickerCoinbaseDto {
    private String type;
    @JsonProperty("product_id")
    private String productId;
    private Double price;
    @JsonProperty("open_24h")
    private Double open24h;
    @JsonProperty("volume_24h")
    private Double volume24h;
    @JsonProperty("low_24h")
    private Double low24h;
    @JsonProperty("high_24h")
    private Double high24h;
    @JsonProperty("close_24h")
    private Double close24h;
    @JsonProperty("best_ask")
    private Double bestAsk;
    @JsonProperty("best_bid")
    private Double bestBid;
    private Side side;
    private Date time;
}
