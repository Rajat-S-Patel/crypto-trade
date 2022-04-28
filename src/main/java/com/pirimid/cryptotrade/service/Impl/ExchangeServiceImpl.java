package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.DTO.ExchangeDto;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.ExcParent;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.repository.ExchangeRepository;
import com.pirimid.cryptotrade.service.ExchangeService;
import com.pirimid.cryptotrade.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ExchangeServiceImpl implements ExchangeService {
    @Autowired
    ExchangeRepository exchangeRepository;
    @Autowired
    ExchangeUtil exchangeUtil;
    @Autowired
    AccountRepository accountrepository;


    @Override
    public List<ExchangeDto> getAllExchanges(UUID userid) {


        List<Exchange> exchanges =  exchangeRepository.findAll();
        List<ExchangeDto> exchangeDtos = new ArrayList<>();
        for(Exchange exchange:exchanges){
            Account account = accountrepository.findAccountByUser_UserIdAndExchange_ExchangeId(userid,exchange.getExchangeId());

                ExchangeDto exchangeDto = ExchangeDto.builder()
                        .name(exchange.getName())
                        .exchangeId(exchange.getExchangeId())
                        .build();
            if (account != null) {
                exchangeDto.setAccountid(account.getAccountId());
            }
            exchangeDtos.add(exchangeDto);
            }
        return exchangeDtos;
    }
    @Override
    public List<Exchange> getAllExchanges() {
        return  exchangeRepository.findAll();

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
