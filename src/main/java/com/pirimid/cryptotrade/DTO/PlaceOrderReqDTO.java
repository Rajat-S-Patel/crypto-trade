package com.pirimid.cryptotrade.DTO;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderReqDTO {
    String accountId;
    String type;    //limit or market
    String symbol;  // BTC/USD
    String price;   // limit order amount per currency
    String fund;    // total amt can be send market order
    String size;    // amount/quantity of currency
    String side;    // buy or sell
}
