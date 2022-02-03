package com.pirimid.cryptotrade.repository;

import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Set<Account> findAllByExchange(Exchange exchange);
}
