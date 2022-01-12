package com.pirimid.cryptotrade.DTO;


import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlaceOrderResDTO {
    String id;
    Double price;
    Double size;
    String symbol; // BTC/ETH
    Side side;
    OrderType type;
    Date createdAt;
    Double executedAmount;
    Status status;
}