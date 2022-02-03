package com.pirimid.cryptotrade.DTO;

import com.pirimid.cryptotrade.model.Side;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TradeDto {
    private String tradeId;
    private String exchangeOrderId;
    private UUID orderId;
    private Double fee;
    private Side side;
    private Double size;
    private Double price;
    private Double funds;
    private Date time;
    private String symbol;
}