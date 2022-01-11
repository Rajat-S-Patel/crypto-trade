package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.repository.ExchangeRepository;
import com.pirimid.cryptotrade.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    @Autowired
    ExchangeRepository exchangeRepository;
    @Override
    public List<Exchange> getAllExchanges() {
        return exchangeRepository.findAll();
    }
}
