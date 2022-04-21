package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "orders", uniqueConstraints = {@UniqueConstraint(name = "unique_excOrder_account",columnNames = {"orderIdExchange","account_id"})})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderId;

    @NotNull
    private String orderIdExchange;

    @NotNull
    private Date startTime;         // time when order is placed
    private Date endTime;           // time when order is completely executed

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id",referencedColumnName = "accountId",nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private Status orderStatus;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;        // limit or market order

    @Enumerated(EnumType.STRING)
    private Side side;              // buy or sell

    private Double filledQuantity;  // amount of order already filled

    @NotNull
    private String symbol;

    private Double price;       // price per currency

    private Double fund;

    private Double orderQty;    // amount of currency to be sold

    private Double commission;

    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Trade> trades = new HashSet<Trade>();

    public Order(OrderResDTO orderDto,Account account){
        this.setOrderIdExchange(orderDto.getExchangeOrderId());
        this.setSide(orderDto.getSide());
        this.setOrderStatus(orderDto.getStatus());
        this.setOrderQty(orderDto.getSize());
        this.setOrderType(orderDto.getType());
        this.setStartTime(orderDto.getCreatedAt());
        this.setSymbol(orderDto.getSymbol());
        this.setPrice(orderDto.getPrice());
        this.setFilledQuantity(orderDto.getExecutedAmount());
        this.setAccount(account);
        this.setFund(orderDto.getFunds());
        this.setOrderStatus(orderDto.getStatus());
    }
}
