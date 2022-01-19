package com.pirimid.cryptotrade.DTO;

import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderReqDTO {
    UUID accountId;
    OrderType type;    //limit or market
    String symbol;  // BTC/USD
    Double price;   // limit order amount per currency
    Double funds;    // total amt can be send market order
    Double size;    // amount/quantity of currency
    Side side;    // buy or sell
}
