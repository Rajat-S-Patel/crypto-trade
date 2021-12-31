package com.pirimid.cryptotrade.model;


import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;
    private String resTradeId;

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
