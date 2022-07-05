package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
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

    private String userIdExchange;      // profile id
    private String userNameExchange;    // profile name
    @NotNull
    private String apiKey;
    @NotNull
    private String secretKey;
    private String passPhrase;
    @NotNull
    private String accountLabel;



    @ManyToOne(targetEntity = Exchange.class)
    @JoinColumn(name = "exchange_id",referencedColumnName = "exchangeId",nullable = false)
    private Exchange exchange;

    @OneToMany(mappedBy = "account",cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Order> orderSet = new HashSet<Order>();
}
