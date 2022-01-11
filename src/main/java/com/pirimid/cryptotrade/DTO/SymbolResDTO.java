package com.pirimid.cryptotrade.DTO;

import lombok.*;

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