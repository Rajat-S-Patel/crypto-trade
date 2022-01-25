package com.pirimid.cryptotrade.controller;

import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable UUID orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) return ResponseEntity.badRequest().body(null);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(order);
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

    @PostMapping("/orders")
    public ResponseEntity<OrderResDTO> createNewOrder(@RequestBody PlaceOrderReqDTO placeOrder) {
        OrderResDTO orderResDTO = orderService.createOrder(placeOrder);
        if (orderResDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(orderResDTO);
    }

    @DeleteMapping("/orders/{orderId}") // ?exchange
    public ResponseEntity<String> cancelOrderById(@PathVariable UUID orderId, @RequestParam(required = true) String exchange) {
        String res = orderService.cancelOrderById(orderId, exchange);
        if (res == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(res);
    }
}
