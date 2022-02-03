package com.pirimid.cryptotrade.repository;

import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderIdExchangeAndAccount(String exchangeOrderId, Account account);
    Optional<Order> findByOrderIdExchangeAndAccount_Exchange(String excOrderId, Exchange exchange);
}
