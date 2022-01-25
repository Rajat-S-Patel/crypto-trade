package com.pirimid.cryptotrade.websocket.coinbase.res;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.UUID;

@Data
@ToString
public class WSCoinbaseOrderDto {
    private String type;
    private String client_oid;
    private String user_id;
    private UUID profile_id;
    private String order_id;
    private String order_type;
    private Double size;
    private String side;
    private String product_id;
    private Date time;
    private Double price;
    private Double funds;
    private String reason;
    private Double remaining_size;
}
