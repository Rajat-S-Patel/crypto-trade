package com.pirimid.cryptotrade.websocket.publicWS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class WsTickerDto {
    private String symbol;
    private Double price;
    private Double volume;
    private Double low;
    private Double high;
    private Double percentChange;

}
