package com.pirimid.cryptotrade.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderReqDTO {
    UUID accountId;
    String type;    //limit or market
    String symbol;  // BTC/USD
    Double price;   // limit order amount per currency
    Double funds;    // total amt can be send market order
    Double size;    // amount/quantity of currency
    String side;    // buy or sell
}
