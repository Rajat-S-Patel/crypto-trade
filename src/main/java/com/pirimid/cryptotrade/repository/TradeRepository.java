package com.pirimid.cryptotrade.repository;

import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TradeRepository extends JpaRepository<Trade, UUID> {
    Optional<Set<Trade>> findAllByOrder(Order order);
}
