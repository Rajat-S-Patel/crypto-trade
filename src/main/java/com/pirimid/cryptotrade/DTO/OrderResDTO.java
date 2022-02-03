package com.pirimid.cryptotrade.DTO;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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