package com.pirimid.cryptotrade.controller;

import com.pirimid.cryptotrade.DTO.BalanceDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.BalanceCoinbaseDTO;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/balance/{accountId}") //?base=BTC&quote=USD
    public ResponseEntity<List<BalanceDTO>> getBalance(@PathVariable(required = true) UUID accountId, @RequestParam(required = false) String base, @RequestParam(required = false) String quote) {
        List<BalanceDTO> balances = orderService.getBalance(accountId);
        if(balances == null)
            return ResponseEntity.badRequest().body(null);
        if(base!=null && quote!=null){
            List<BalanceDTO> filteredBalances =
            balances.stream()
                    .filter(balance->balance.getCurrency().equals(base.toUpperCase()) || balance.getCurrency().equals(quote.toUpperCase()))
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(filteredBalances);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(balances);
    }
    @GetMapping("/orders/all") // orders/all?exchange=
    public ResponseEntity<Set<Order>> getAllOrders(@RequestParam(required = false) String exchange) {
        Set<Order> orders;
        if (exchange.isEmpty())
            orders = orderService.getAllOrders();
        else
            orders = orderService.getAllOrders(exchange);
        if (orders == null || orders.isEmpty()) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(orders);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<Set<OrderResDTO>> getOrdersByUser(@PathVariable(required = true) UUID userId) {
        Set<OrderResDTO> orders = orderService.getOrdersByUserId(userId);
        if(orders == null)
            return ResponseEntity.badRequest().body(null);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(orders);
    }
    @PostMapping("/orders")
    public ResponseEntity<OrderResDTO> createNewOrder(@RequestBody PlaceOrderReqDTO placeOrder) {
        OrderResDTO order = orderService.createOrder(placeOrder);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(order);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<String> cancelOrderById(@PathVariable UUID orderId) {
        String res = orderService.cancelOrderById(orderId);
        if (res == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
    }
}
