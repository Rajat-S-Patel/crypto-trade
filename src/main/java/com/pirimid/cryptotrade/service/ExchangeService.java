package com.pirimid.cryptotrade.service;

import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.model.Exchange;

import java.util.List;

public interface ExchangeService {
    List<Exchange> getAllExchanges();
    List<SymbolResDTO> getPairs(String exchangeName);
    void fetchAllPairs();
}
