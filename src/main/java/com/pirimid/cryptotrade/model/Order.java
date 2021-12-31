package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @NotNull
    private String resOrderId;

    @NotNull
    private Date startTime;
    private Date endTime;

    @ManyToOne(targetEntity = Account.class)
    @JoinColumn(name = "account_id",referencedColumnName = "accountId",nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    private Status orderStatus;

    @Enumerated(EnumType.STRING)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    private Side side;

    private Double filledQuantity;

    @NotNull
    private String symbol;
    @NotNull
    private Double price;

    @NotNull
    private Double orderQty;

    private Double commission;

    @OneToMany(mappedBy = "tradeId")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Trade> trades = new HashSet<Trade>();

}
