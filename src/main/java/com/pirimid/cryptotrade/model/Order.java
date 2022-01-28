package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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

    @OneToMany(mappedBy = "tradeId")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Trade> trades = new HashSet<Trade>();

}
