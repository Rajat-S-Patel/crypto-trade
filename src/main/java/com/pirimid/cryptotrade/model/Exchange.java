package com.pirimid.cryptotrade.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
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
