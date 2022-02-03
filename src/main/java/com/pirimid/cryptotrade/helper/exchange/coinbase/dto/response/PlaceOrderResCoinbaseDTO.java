package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceOrderResCoinbaseDTO {

    private UUID id;
    private Double price;
    private Double size;
    @JsonProperty("product_id")
    private String productId;
    private String side;
    private String type;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("end_at")
    private Date endAt;
    private String status;
    @JsonProperty("filled_size")
    private String filledSize;
    @JsonProperty("executed_value")
    private Double executedValue;
    private Double funds;

}
