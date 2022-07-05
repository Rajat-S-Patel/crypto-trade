package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BalanceCoinbaseDTO {
    private UUID id;
    private String currency;
    private float balance;
    private float available;
    private float hold;
    @JsonProperty("profile_id")
    private UUID accountExchangeId;
}
