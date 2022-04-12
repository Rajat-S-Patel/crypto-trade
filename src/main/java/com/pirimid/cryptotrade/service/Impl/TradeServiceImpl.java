package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.Trade;
import com.pirimid.cryptotrade.repository.TradeRepository;
import com.pirimid.cryptotrade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
@Service
public class TradeServiceImpl implements TradeService {
    @Autowired
    TradeRepository tradeRepository;
    @Override
    public Set<Trade> getTradesByOrderId(Order order) {
       return tradeRepository.findAllByOrder(order).orElse(null);
    }

}
