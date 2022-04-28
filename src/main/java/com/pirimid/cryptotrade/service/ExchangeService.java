package com.pirimid.cryptotrade.service;

import com.pirimid.cryptotrade.DTO.ExchangeDto;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.model.Exchange;

import java.util.List;
import java.util.UUID;

public interface ExchangeService {

    List<ExchangeDto> getAllExchanges(UUID userid);

    List<Exchange> getAllExchanges();
    List<SymbolResDTO> getPairs(String exchangeName);
    void fetchAllPairs();
}
