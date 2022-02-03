package com.pirimid.cryptotrade.service;

import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.model.Order;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
public interface OrderService {
    Order getOrderById(UUID id);
    Set<Order> getOrderByAccount(UUID accountId);
    Set<Order> getAllOrders();
    Set<Order> getAllOrders(String exchange);
    OrderResDTO createOrder(PlaceOrderReqDTO order);
    OrderResDTO createOrder(OrderResDTO order);
    OrderResDTO addTrade(TradeDto trade,EXCHANGE exchange);
    OrderResDTO completeOrder(OrderResDTO orderResDTO);
    String rejectOrderByExchangeOrderId(String excOrderId, EXCHANGE exchangeName, Date timestamp);
    String cancelOrderByExchangeOrderId(String excOrderId, EXCHANGE exchangeName, Date timestamp);
    String cancelOrderById(UUID id,String exchangeName);
}
