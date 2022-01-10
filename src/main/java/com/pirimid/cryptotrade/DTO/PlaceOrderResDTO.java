package com.pirimid.cryptotrade.DTO;


import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderResDTO {
    String id;
    String price;
    String size;
    String symbol; // BTC/ETH
    String side;
    String type;
    String createdAt;
    String executed_amount;
    String status;
}
