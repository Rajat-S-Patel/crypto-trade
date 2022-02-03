package com.pirimid.cryptotrade.repository;

import com.pirimid.cryptotrade.model.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, UUID> {
    Optional<Exchange> findByName(String name);
}
