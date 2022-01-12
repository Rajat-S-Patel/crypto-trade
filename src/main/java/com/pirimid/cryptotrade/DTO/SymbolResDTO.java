package com.pirimid.cryptotrade.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SymbolResDTO {
    String symbol;
    String base;
    String quote;
    Double minOrderSize;
    Double minMarketFunds;

}