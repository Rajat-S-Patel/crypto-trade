package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID accountId;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id",referencedColumnName = "userId")
    private User user;

    private String userIdExchange;

    @NotNull
    private String apiKey;
    @NotNull
    private String secretKey;
    private String passPhrase;



    @ManyToOne(targetEntity = Exchange.class)
    @JoinColumn(name = "exchange_id",referencedColumnName = "exchangeId",nullable = false)
    private Exchange exchange;

    @OneToMany(mappedBy = "account")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Order> orderSet = new HashSet<Order>();
}
