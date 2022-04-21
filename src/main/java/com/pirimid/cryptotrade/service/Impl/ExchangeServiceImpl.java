package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.ExcParent;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.repository.ExchangeRepository;
import com.pirimid.cryptotrade.service.ExchangeService;
import com.pirimid.cryptotrade.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    @Autowired
    ExchangeRepository exchangeRepository;
    @Autowired
    ExchangeUtil exchangeUtil;

    @Override
    public List<Exchange> getAllExchanges() {
        return exchangeRepository.findAll();
    }
    private ExcParent getExchangeObject(String exchangeName){
        return exchangeUtil.getObject(EXCHANGE.valueOf(exchangeName.toUpperCase()));
    }

    @Override
    public List<SymbolResDTO> getPairs(String exchangeName) {
        return getExchangeObject(exchangeName).getPairs();
    }

    @Override
    public void fetchAllPairs() {
        for(Exchange exchange:getAllExchanges()){
            getExchangeObject(exchange.getName()).fetchPairs();
        }
    }
}
