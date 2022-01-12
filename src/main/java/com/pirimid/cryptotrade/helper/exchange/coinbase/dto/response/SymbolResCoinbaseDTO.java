package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymbolResCoinbaseDTO {
        String id;
        String base_currency;
        String quote_currency;
        Double base_min_size;
        Double min_market_funds;
}
