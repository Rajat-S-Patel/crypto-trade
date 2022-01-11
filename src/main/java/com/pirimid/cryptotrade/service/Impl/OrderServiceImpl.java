package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.helper.EXCHANGE;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.Status;
import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.repository.OrderRepository;
import com.pirimid.cryptotrade.repository.UserRepository;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExchangeUtil exchangeUtil;
    @Autowired
    User user;
    @Override
    public Order getOrderById(UUID id) {
        Optional<Order> order = orderRepository.findById(id);
        if(order.isEmpty()) return null;
        return order.get();
    }

    @Override
    public Set<Order> getOrderByAccount(UUID accountId) {
        Optional<Account> optAccount = accountRepository.findById(accountId);
        if(optAccount.isPresent()){
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
            EXCHANGE exchange = EXCHANGE.valueOf(exchangeName);
//            User user = new User();
            Set<Order> orders = new HashSet<>();
            user.getAccountSet()
                    .stream()
                    .filter(account -> account.getExchange().equals(exchange))
                    .forEach(account -> orders.addAll(account.getOrderSet()));
            return orders;
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createOrder(PlaceOrderReqDTO req) {
        Optional<Account> optAccount = accountRepository.findById(req.getAccountId());
        if(optAccount.isPresent()) {
            Account account = optAccount.get();
            Order order = new Order();
            //api call
            exchangeUtil
                    .getObject(EXCHANGE.valueOf(account.getExchange().getName().toUpperCase()))
                    .createOrder(account.getApiKey(),account.getSecretKey(), account.getPassPhrase(), req);
            orderRepository.save(order);
        }
    }

    @Override
    public boolean cancelOrderById(UUID id,String exchangeName) {
//        User user = userRepository.findById(user.getUserId()).get();
        EXCHANGE exchange = EXCHANGE.valueOf(exchangeName);
        Optional<Order> optOrder = orderRepository.findById(id);
        if(optOrder.isPresent()){
            Account account = optOrder.get().getAccount();
            if(exchangeUtil
                    .getObject(exchange)
                    .cancelOrder(account.getApiKey(),account.getSecretKey(),account.getPassPhrase(),optOrder.get().getOrderIdExchange())) {
                optOrder.get().setOrderStatus(Status.CANCELLED);
                orderRepository.save(optOrder.get());
                return true;
            }else{
                // cancel order failed
                return false;
            }
        }
        return false;
    }
}
