package com.pirimid.cryptotrade.service;

import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.Trade;

import java.util.Set;

public interface TradeService {
    Set<Trade> getTradesByOrderId(Order order);
}
