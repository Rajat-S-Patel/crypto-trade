package com.pirimid.cryptotrade.model;


import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
