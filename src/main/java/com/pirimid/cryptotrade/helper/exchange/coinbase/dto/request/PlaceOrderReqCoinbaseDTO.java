package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ToString
public class PlaceOrderReqCoinbaseDTO {
    private  String type;    //limit or market
    private String product_id;  // BTC/USD
    private Double price;   // limit order amount per currency
    private Double funds;    // total amt can be send market order
    private Double size;    // amount/quantity of currency
    private String side;    // buy or sell

}
