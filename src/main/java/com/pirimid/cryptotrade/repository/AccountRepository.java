package com.pirimid.cryptotrade.repository;

import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Set<Account> findAllByExchange(Exchange exchange);

    @Query(nativeQuery = true,name = "accountByUserAndExchange",value = "SELECT *FROM account WHERE user_id = :userId AND exchange_id = :exchangeId")
    Account findAccountByUserIdAndExchangeId(UUID userId,UUID exchangeId);

    Optional<Set<Account>> findByUser(User user);

}
