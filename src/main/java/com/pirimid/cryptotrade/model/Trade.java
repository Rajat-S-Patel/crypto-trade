package com.pirimid.cryptotrade.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID tradeId;
    private String tradeIdExchange;

    @ManyToOne(targetEntity = Order.class)
    @JoinColumn(name = "order_id",referencedColumnName = "orderId")
    @JsonIgnore
    private Order order;

    @NotNull
    private Double amount;

    @NotNull
    private Date timestamp;
    @NotNull
    private Double quantity;
    @NotNull
    private Double marketPrice;
}
