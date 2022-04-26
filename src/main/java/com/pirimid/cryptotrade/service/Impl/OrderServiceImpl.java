package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.DTO.AccountDTO;
import com.pirimid.cryptotrade.DTO.BalanceDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.exception.AccountNotFoundException;
import com.pirimid.cryptotrade.exception.OrderFailedException;
import com.pirimid.cryptotrade.exception.OrderNotFoundException;
import com.pirimid.cryptotrade.exception.InvalidApiKeyException;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.BalanceCoinbaseDTO;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Status;
import com.pirimid.cryptotrade.model.Trade;
import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.ExchangeRepository;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.repository.UserRepository;
import com.pirimid.cryptotrade.repository.TradeRepository;
import com.pirimid.cryptotrade.repository.OrderRepository;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.service.TradeService;
import com.pirimid.cryptotrade.service.UserService;
import com.pirimid.cryptotrade.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    SimpMessagingTemplate messagingTemplate;
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
    UserService userService;
    @Autowired
    AccountService accountService;
    @Autowired
    TradeService tradeService;
    User user=null;
    @PostConstruct
    private void postConstructor(){
        user = userService.getDefaultUser();
    }
    private Order orderResDtoToOrder(OrderResDTO orderDto,Account account){
        Order newOrder = new Order(orderDto,account);
        return newOrder;
    }
    private OrderResDTO orderToOrderResDto(Order order){
        OrderResDTO orderResDTO = new OrderResDTO(order);
        return  orderResDTO;
    }
    private void sendOrderUpdateToClient(OrderResDTO orderResDTO){
        messagingTemplate.convertAndSend("/topic/order/"+user.getUserId().toString(),orderResDTO);
    }
    @Override
    public Order getOrderById(UUID id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) return null;
        return order.get();
    }

    @Override
    public Set<OrderResDTO> getOrdersByUserId(UUID userId) {
        User user = userService.getUserById(userId);
        if(user == null) return null;
        Optional<List<Order>> orders = orderRepository.findAllByAccount_User(user);
        if(orders.isPresent()){
            Set<OrderResDTO> orderResponses = new HashSet<>();
            orders.get()
                    .stream()
                    .forEach(order->{
                        OrderResDTO orderRes = orderToOrderResDto(order);
                        Set<Trade>  trades = order.getTrades();
                        orderRes.setTrades(trades);
                        orderRes.setFee(order.getCommission());
                        orderRes.setExchange(order.getAccount().getExchange());
                        orderResponses.add(orderRes);
                    });
            return orderResponses;
        }
        return null;
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
        try {
            // TODO replace accountId with userId (create order with userId)
            System.out.println(req.getAccountId());
            Optional<Account> optAccount = accountRepository.findById(req.getAccountId());
            if (!optAccount.isPresent()) {
                throw new AccountNotFoundException("No such account exist in database");
            }
            Account account = optAccount.get();
            OrderResDTO orderResDTO = exchangeUtil
                    .getObject(EXCHANGE.valueOf(account.getExchange().getName().toUpperCase()))
                    .createOrder(account.getApiKey(), account.getSecretKey(), account.getPassPhrase(), req);
            orderResDTO.setAccountId(optAccount.get().getAccountId());
            return orderResDTO;
        } catch(AccountNotFoundException e){
          throw e;
        } catch (OrderFailedException e){
            throw e;
        }
    }

    @Override
    public OrderResDTO createOrder(OrderResDTO orderDto) {
        Optional<Account> optAccount = accountRepository.findById(orderDto.getAccountId());
        if (!optAccount.isPresent()) {
            return null;
        }
        Optional<Order> order = orderRepository.findByOrderIdExchangeAndAccount_Exchange(orderDto.getExchangeOrderId(), optAccount.get().getExchange()); // accoount->exchange
        if (order.isPresent()) {
            orderDto.setOrderId(order.get().getOrderId());
            return orderDto;
        }
        Order newOrder = orderResDtoToOrder(orderDto,optAccount.get());
        newOrder = orderRepository.save(newOrder);
        orderDto.setOrderId(newOrder.getOrderId());
        orderDto.setExchange(optAccount.get().getExchange());
        this.sendOrderUpdateToClient(orderDto);
        return orderDto;
    }

    @Override
    public OrderResDTO addTrade(TradeDto tradeDto,EXCHANGE exchange) {
        Optional<Exchange> optExchange = exchangeRepository.findByName(exchange.getValue().toLowerCase());
        if(!optExchange.isPresent()) return null;
        Optional<Order> optOrder = orderRepository.findByOrderIdExchangeAndAccount_Exchange(tradeDto.getExchangeOrderId(),optExchange.get()); // add exchange name
        if (!optOrder.isPresent()) return null;     // no such order exists

        Trade trade = new Trade();
        trade.setAmount(tradeDto.getFunds());
        trade.setMarketPrice(tradeDto.getPrice());
        trade.setQuantity(tradeDto.getSize());
        trade.setTimestamp(tradeDto.getTime());
        trade.setTradeIdExchange(tradeDto.getTradeId());
        trade.setFee(tradeDto.getFee());
        Order order = optOrder.get();
        order.getTrades().add(trade);
        order.setCommission((order.getCommission() == null ? 0 : order.getCommission()) + tradeDto.getFee());
        order.setOrderStatus(Status.PARTIALLY_FILLED);
        if(order.getOrderType().equals(OrderType.MARKET)){
            Double prevPrice = order.getPrice()==null?0:order.getPrice();
            order.setPrice((prevPrice*(order.getTrades().size()-1) + trade.getMarketPrice())/(order.getTrades().size()));
        }
        if (order.getOrderType().equals(OrderType.MARKET))
            order.setFilledQuantity((order.getFilledQuantity() == null ? 0 : order.getFilledQuantity()) + (order.getOrderQty() == null ? tradeDto.getFunds() : tradeDto.getSize()));
        else if(order.getOrderType().equals(OrderType.LIMIT))
            order.setFilledQuantity((order.getFilledQuantity() == null ? 0 : order.getFilledQuantity()) + tradeDto.getSize());

        order = orderRepository.save(order);
        tradeDto.setOrderId(order.getOrderId());
        trade.setOrder(order);
        tradeRepository.save(trade);
        OrderResDTO orderResDTO = this.orderToOrderResDto(order);
        this.sendOrderUpdateToClient(orderResDTO);
        return orderResDTO;
    }

    @Override
    public OrderResDTO completeOrder(OrderResDTO orderDto) {
        Optional<Account> optAccount = accountRepository.findById(orderDto.getAccountId());
        if(!optAccount.isPresent()) return null;
        Optional<Order> order = orderRepository.findByOrderIdExchangeAndAccount(orderDto.getExchangeOrderId(), optAccount.get());
        if (order.isPresent()) {
            order.get().setOrderStatus(orderDto.getStatus());
            order.get().setEndTime(orderDto.getEndAt());
            orderDto.setOrderId(order.get().getOrderId());
            orderDto.setExchange(order.get().getAccount().getExchange());
            this.sendOrderUpdateToClient(orderDto);
            orderRepository.save(order.get());
            return orderDto;
        }
        return null;
    }
    @Override
    public String rejectOrderByExchangeOrderId(String excOrderId, EXCHANGE exchange, Date timestamp) {
        Optional<Exchange> optExchange = exchangeRepository.findByName(exchange.getValue().toLowerCase());
        if (!optExchange.isPresent()) return null;
        Optional<Order> optOrder = orderRepository.findByOrderIdExchangeAndAccount_Exchange(excOrderId, optExchange.get());
        if (!optOrder.isPresent()) return null;
        Order order = optOrder.get();
        order.setOrderStatus(Status.REJECTED);
        order.setEndTime(timestamp);
        this.sendOrderUpdateToClient(orderToOrderResDto(order));
        orderRepository.save(order);
        return order.getOrderId().toString();
    }
    @Override
    public String cancelOrderByExchangeOrderId(String excOrderId, EXCHANGE exchange, Date timestamp) {
        Optional<Exchange> optExchange = exchangeRepository.findByName(exchange.getValue().toLowerCase());
        if (!optExchange.isPresent()) return null;
        Optional<Order> optOrder = orderRepository.findByOrderIdExchangeAndAccount_Exchange(excOrderId, optExchange.get());
        if (!optOrder.isPresent()) return null;
        Order order = optOrder.get();
        order.setOrderStatus(Status.CANCELLED);
        order.setEndTime(timestamp);
        this.sendOrderUpdateToClient(orderToOrderResDto(order));
        orderRepository.save(order);
        return order.getOrderId().toString();
    }

    @Override
    public String cancelOrderById(UUID id) {
        Optional<Order> optOrder = orderRepository.findById(id);
        if (optOrder.isPresent()) {
            Account account = optOrder.get().getAccount();
            EXCHANGE exchange = EXCHANGE.valueOf(account.getExchange().getName().toUpperCase());
            if (exchangeUtil
                    .getObject(exchange)
                    .cancelOrder(account.getApiKey(), account.getSecretKey(), account.getPassPhrase(), optOrder.get().getOrderIdExchange())) {
                optOrder.get().setOrderStatus(Status.CANCELLED);
                this.sendOrderUpdateToClient(orderToOrderResDto(optOrder.get()));
                orderRepository.save(optOrder.get());
                return String.valueOf(id);
            } else {
                // cancel order failed
                return null;
            }
        }
        throw new OrderNotFoundException("No such order exist in database");
    }
    public List<BalanceDTO> getBalance(UUID accountId) {
        try {
            Optional<Account> optAccount = accountRepository.findById(accountId);
            if (!optAccount.isPresent()) {
                throw new AccountNotFoundException("No such account exist in database");
            }
            Account account = optAccount.get();
            List<BalanceDTO> balanceDTOS = exchangeUtil
                    .getObject(EXCHANGE.valueOf(account.getExchange().getName().toUpperCase()))
                    .getBalance(account.getApiKey(), account.getSecretKey(), account.getPassPhrase());
            return balanceDTOS;
        } catch(AccountNotFoundException e){
            throw e;
        } catch (InvalidApiKeyException e){
            throw e;
        }
    }
    public String getBalance(AccountDTO accountDTO) {
        try {
            List<BalanceDTO> balanceDTOS = exchangeUtil
                    .getObject(EXCHANGE.valueOf(accountDTO.getExchange().getName().toUpperCase()))
                    .getBalance(accountDTO.getApiKey(), accountDTO.getSecretKey(), accountDTO.getPassPhrase());
            if(balanceDTOS == null){
                return "not verified";
            }
            return "verified";
        } catch (InvalidApiKeyException e){
            e.printStackTrace();
        }catch ( Exception e){
            e.printStackTrace();
        }
        return "not verified";
    }
}
