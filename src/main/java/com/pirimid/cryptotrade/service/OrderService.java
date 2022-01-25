package com.pirimid.cryptotrade.service;

import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.model.Order;

import java.util.Set;
import java.util.UUID;

public interface OrderService {
    Order getOrderById(UUID id);
    Set<Order> getOrderByAccount(UUID accountId);
    Set<Order> getAllOrders();
    Set<Order> getAllOrders(String exchange);
    OrderResDTO createOrder(PlaceOrderReqDTO order);
    String cancelOrderById(UUID id,String exchangeName);
}
