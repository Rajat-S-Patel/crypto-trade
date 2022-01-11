package com.pirimid.cryptotrade.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderResDTO {
    String id;
    Double price;
    Double size;
    String symbol; // BTC/ETH
    String side;
    String type;
    Date createdAt;
    Double executed_amount;
    String status;
}