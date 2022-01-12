package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import com.pirimid.cryptotrade.model.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
    private Double price;
    private Double size;
    private String product_id;
    private String side;
    private String type;
    private Date created_at;
    private Date end_at;
    private String status;
    private String filled_size;
    private Double executed_value;

}
