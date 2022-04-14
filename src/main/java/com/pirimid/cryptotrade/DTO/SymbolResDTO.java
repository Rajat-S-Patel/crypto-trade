package com.pirimid.cryptotrade.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SymbolResDTO {
    String symbol;
    String base;
    String quote;
    Double minOrderSize;
    Double minMarketFunds;

}