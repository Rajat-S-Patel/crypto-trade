package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.model.*;
import com.pirimid.cryptotrade.repository.*;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ExchangeRepository exchangeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TradeRepository tradeRepository;
    @Autowired
    ExchangeUtil exchangeUtil;
    @Autowired
    User user;

    @Override
    public Order getOrderById(UUID id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) return null;
        return order.get();
    }

    @Override
    public Set<Order> getOrderByAccount(UUID accountId) {
        Optional<Account> optAccount = accountRepository.findById(accountId);
        if (optAccount.isPresent()) {
            return optAccount.get().getOrderSet();
        }
        return null;
    }

    @Override
    public Set<Order> getAllOrders() {
        User user = new User();
        Set<Order> orders = new HashSet<>();
        user.getAccountSet().forEach(account -> {
            orders.addAll(account.getOrderSet());
        });
        return orders;
    }

    @Override
    public Set<Order> getAllOrders(String exchangeName) {
        try {
            EXCHANGE exchange = EXCHANGE.valueOf(exchangeName.toUpperCase());
            Set<Order> orders = new HashSet<>();
            user.getAccountSet()
                    .stream()
                    .filter(account -> account.getExchange().equals(exchange))
                    .forEach(account -> orders.addAll(account.getOrderSet()));
            return orders;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OrderResDTO createOrder(PlaceOrderReqDTO req) {
        Optional<Account> optAccount = accountRepository.findById(req.getAccountId());
        if (!optAccount.isPresent()) {
            return null;
        }
        Account account = optAccount.get();
        //api call
        OrderResDTO orderResDTO = exchangeUtil
                .getObject(EXCHANGE.valueOf(account.getExchange().getName().toUpperCase()))
                .createOrder(account.getApiKey(), account.getSecretKey(), account.getPassPhrase(), req);
        if (orderResDTO == null) {
            return null;
        }
//            Optional<Order> optOrder = orderRepository.findByOrderIdExchangeAndAccount(orderResDTO.getExchangeOrderId(),optAccount.get());
//            if(optOrder.isPresent()) return orderResDTO;
//            Order order = new Order();
//            order.setOrderIdExchange(orderResDTO.getExchangeOrderId());
//            order.setSide(orderResDTO.getSide());
//            order.setOrderStatus(orderResDTO.getStatus());
//            order.setOrderQty(orderResDTO.getSize());
//            order.setOrderType(orderResDTO.getType());
//            order.setStartTime(orderResDTO.getCreatedAt());
//            order.setSymbol(orderResDTO.getSymbol());
//            order.setPrice(orderResDTO.getPrice());
//            order.setFilledQuantity(orderResDTO.getExecutedAmount());
//            order.setAccount(account);
//            order.setFund(orderResDTO.getFunds());
//            order = orderRepository.save(order);
//            orderResDTO.setOrderId(order.getOrderId());
        orderResDTO.setAccountId(optAccount.get().getAccountId());

        return orderResDTO;

    }

    @Override
    public OrderResDTO createOrder(OrderResDTO orderDto, String exchange) {
        Optional<Exchange> optExchange = exchangeRepository.findByName(exchange);
        if (!optExchange.isPresent()) return null;
        Optional<Account> optAccount = accountRepository.findByUserIdExchangeAndExchange(orderDto.getExchangeUserId(), optExchange.get());
        if (!optAccount.isPresent()) return null;
        Optional<Order> order = orderRepository.findByOrderIdExchangeAndAccount(orderDto.getExchangeOrderId(), optAccount.get()); // accoount->exchange
        if (order.isPresent()) {
//            order.get().setOrderStatus(orderDto.getStatus());
            orderDto.setOrderId(order.get().getOrderId());
//            orderRepository.save(order.get());
            return orderDto;
        }
        Order newOrder = new Order();
        newOrder.setOrderIdExchange(orderDto.getExchangeOrderId());
        newOrder.setSide(orderDto.getSide());
        newOrder.setOrderStatus(orderDto.getStatus());
        newOrder.setOrderQty(orderDto.getSize());
        newOrder.setOrderType(orderDto.getType());
        newOrder.setStartTime(orderDto.getCreatedAt());
        newOrder.setSymbol(orderDto.getSymbol());
        newOrder.setPrice(orderDto.getPrice());
        newOrder.setFilledQuantity(orderDto.getExecutedAmount());
        newOrder.setAccount(optAccount.get());
        newOrder.setFund(orderDto.getFunds());
        newOrder.setOrderStatus(Status.NEW);
        newOrder = orderRepository.save(newOrder);
        orderDto.setOrderId(newOrder.getOrderId());
        orderDto.setAccountId(newOrder.getAccount().getAccountId());
        return orderDto;
    }

    @Override
    public OrderResDTO addTrade(TradeDto tradeDto) {
        Optional<Order> optOrder = orderRepository.findByOrderIdExchange(tradeDto.getExchangeOrderId()); // add exchange name
        if (!optOrder.isPresent()) return null;     // no such order exists
//        Optional<Trade> optTrade = tradeRepository.findByTradeIdExchangeAndOrder_OrderIdExchange(tradeDto.getTradeId(),tradeDto.getExchangeOrderId());
//        if(optTrade.isPresent()){
//            return tradeDto;
//        }
        Trade trade = new Trade();
        trade.setAmount(tradeDto.getFunds());
        trade.setMarketPrice(tradeDto.getPrice());
        trade.setQuantity(tradeDto.getSize());
        trade.setTimestamp(tradeDto.getTime());
        trade.setTradeIdExchange(tradeDto.getTradeId());

        Order order = optOrder.get();
        order.setCommission((order.getCommission() == null ? 0 : order.getCommission()) + tradeDto.getFee());
        order.setOrderStatus(Status.PARTIALLY_FILLED);

        if (order.getOrderType().equals(OrderType.MARKET))
            order.setFilledQuantity((order.getFilledQuantity() == null ? 0 : order.getFilledQuantity()) + (order.getOrderQty() == null ? tradeDto.getFunds() : tradeDto.getSize()));
         else //limit
            order.setFilledQuantity((order.getFilledQuantity() == null ? 0 : order.getFilledQuantity()) + tradeDto.getSize());

        order = orderRepository.save(order);
        tradeDto.setOrderId(order.getOrderId());
        trade.setOrder(order);
        trade = tradeRepository.save(trade);

        OrderResDTO orderResDTO = new OrderResDTO();
        orderResDTO.setExchangeOrderId(order.getOrderIdExchange());
        orderResDTO.setOrderId(order.getOrderId());
        orderResDTO.setPrice(order.getPrice());
        orderResDTO.setSize(order.getOrderQty());
        orderResDTO.setFunds(order.getFund());
        orderResDTO.setSymbol(order.getSymbol());
        orderResDTO.setSide(order.getSide());
        orderResDTO.setType(order.getOrderType());
        orderResDTO.setCreatedAt(order.getStartTime());
        orderResDTO.setEndAt(order.getEndTime());
        orderResDTO.setExecutedAmount(order.getFilledQuantity());
        orderResDTO.setStatus(order.getOrderStatus());
        orderResDTO.setAccountId(order.getAccount().getAccountId());

        return orderResDTO;
    }

    @Override
    public OrderResDTO completeOrder(OrderResDTO orderDto, String exchange) {
        Optional<Exchange> optExchange = exchangeRepository.findByName(exchange);
        if (!optExchange.isPresent()) return null;
        Optional<Account> optAccount = accountRepository.findByUserIdExchangeAndExchange(orderDto.getExchangeUserId(), optExchange.get());
        Optional<Order> order = orderRepository.findByOrderIdExchangeAndAccount(orderDto.getExchangeOrderId(), optAccount.get());
        if (order.isPresent()) {
            order.get().setOrderStatus(orderDto.getStatus());
            order.get().setEndTime(orderDto.getEndAt());
            orderDto.setOrderId(order.get().getOrderId());
            orderRepository.save(order.get());
            return orderDto;
        }
        return null;
    }
    @Override
    public String rejectOrderByExchangeOrderId(String excOrderId, String exchangeName, Date timestamp) {
        Optional<Exchange> optExchange = exchangeRepository.findByName(exchangeName);
        if (!optExchange.isPresent()) return null;
        Optional<Order> optOrder = orderRepository.findByOrderIdExchangeAndAccount_Exchange(excOrderId, optExchange.get());
        if (!optOrder.isPresent()) return null;
        Order order = optOrder.get();
        order.setOrderStatus(Status.REJECTED);
        order.setEndTime(timestamp);
        orderRepository.save(order);
        return order.getOrderId().toString();
    }
    @Override
    public String cancelOrderByExchangeOrderId(String excOrderId, String exchangeName, Date timestamp) {
        Optional<Exchange> optExchange = exchangeRepository.findByName(exchangeName);
        if (!optExchange.isPresent()) return null;
        Optional<Order> optOrder = orderRepository.findByOrderIdExchangeAndAccount_Exchange(excOrderId, optExchange.get());
        if (!optOrder.isPresent()) return null;
        Order order = optOrder.get();
        order.setOrderStatus(Status.CANCELLED);
        order.setEndTime(timestamp);
        orderRepository.save(order);
        return order.getOrderId().toString();
    }

    @Override
    public String cancelOrderById(UUID id, String exchangeName) {
        EXCHANGE exchange = EXCHANGE.valueOf(exchangeName.toUpperCase());
        Optional<Order> optOrder = orderRepository.findById(id);
        if (optOrder.isPresent()) {
            Account account = optOrder.get().getAccount();

            if (exchangeUtil
                    .getObject(exchange)
                    .cancelOrder(account.getApiKey(), account.getSecretKey(), account.getPassPhrase(), optOrder.get().getOrderIdExchange())) {
                optOrder.get().setOrderStatus(Status.CANCELLED);
                orderRepository.save(optOrder.get());
                return String.valueOf(id);
            } else {
                // cancel order failed
                return null;
            }
        }
        return null;
    }
}
