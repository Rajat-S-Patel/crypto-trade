package com.pirimid.cryptotrade.service;

import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Order;

import java.util.Set;
import java.util.UUID;

public interface AccountService {
    Set<Account> getAllAccounts();
    Set<Account> getAllAccountsByExchangeName(String name);
    Account getAccountById(UUID id);
    Set<Order> getOrderByAccount(UUID id);
    void setProfileIdDetails(UUID id,String profileId,String profileName);
}
