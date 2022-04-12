package com.pirimid.cryptotrade.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pirimid.cryptotrade.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResDTO {
    String exchangeOrderId;
    String exchangeUserId;
    UUID orderId;
    Double price;
    Double size;
    Double funds;
    String symbol; // BTC/ETH
    Side side;
    OrderType type;
    Date createdAt;
    Date endAt;
    Double executedAmount;
    Status status;
    UUID accountId;
    Exchange exchange;
    Set<Trade> trades = null;

    public OrderResDTO(Order order){
        this.setExchangeOrderId(order.getOrderIdExchange());
        this.setExchangeUserId(order.getAccount().getUserIdExchange());
        this.setOrderId(order.getOrderId());
        this.setPrice(order.getPrice());
        this.setSize(order.getOrderQty());
        this.setFunds(order.getFund());
        this.setSymbol(order.getSymbol());
        this.setSide(order.getSide());
        this.setType(order.getOrderType());
        this.setCreatedAt(order.getStartTime());
        this.setEndAt(order.getEndTime());
        this.setExecutedAmount(order.getFilledQuantity());
        this.setStatus(order.getOrderStatus());
        this.setAccountId(order.getAccount().getAccountId());
    }
}