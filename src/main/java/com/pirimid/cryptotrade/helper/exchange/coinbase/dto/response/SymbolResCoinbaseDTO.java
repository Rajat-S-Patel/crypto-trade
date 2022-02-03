package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SymbolResCoinbaseDTO {
        String id;
        @JsonProperty("base_currency")
        String baseCurrency;
        @JsonProperty("quote_currency")
        String quoteCurrency;
        @JsonProperty("base_min_size")
        Double baseMinSize;
        @JsonProperty("min_market_funds")
        Double minMarketFunds;
}
