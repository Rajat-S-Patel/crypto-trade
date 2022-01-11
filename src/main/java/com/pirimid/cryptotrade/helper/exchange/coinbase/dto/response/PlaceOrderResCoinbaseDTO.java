package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ToString
public class PlaceOrderResCoinbaseDTO {

    private UUID id;
    private double price;
    private double size;
    private String product_id;
    private String side;
    private String type;
    private Date created_at;
    private Date end_at;
    private String status;
    private String filled_size;
    private double executed_value;

}
