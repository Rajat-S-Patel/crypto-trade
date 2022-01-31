package com.pirimid.cryptotrade.websocket.coinbase.res;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class WSCoinbaseTradeDto {
    private String type;
    private String trade_id;
    private String taker_order_id;
    private String maker_order_id;
    private String taker_profile_id;
    private String taker_user_id;
    private Double taker_fee_rate;
    private String user_id;
    private String profile_id;
    private Double size;
    private String side;
    private String product_id;
    private Date time;
    private Double price;
    private Double funds;
    private Double remaining_size;

}
