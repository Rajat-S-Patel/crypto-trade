package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class Exchange {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID exchangeId;

    @Column(unique = true,nullable = false)
    private String name;

    @OneToMany(mappedBy = "exchange")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Account> accountSet = new HashSet<Account>();
}
