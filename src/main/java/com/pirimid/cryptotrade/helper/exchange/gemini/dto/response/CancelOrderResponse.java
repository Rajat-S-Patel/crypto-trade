package com.pirimid.cryptotrade.helper.exchange.gemini.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelOrderResponse {

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("is_cancelled")
    private boolean isCancelled;


}
