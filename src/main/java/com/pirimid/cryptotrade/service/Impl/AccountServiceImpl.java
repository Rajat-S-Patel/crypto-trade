package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.repository.ExchangeRepository;
import com.pirimid.cryptotrade.repository.UserRepository;
import com.pirimid.cryptotrade.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExchangeRepository exchangeRepository;
    @Autowired
    User user;

    @Override
    public Set<Account> getAllAccounts() {
        return user.getAccountSet();
    }

    @Override
    public Set<Account> getAllAccountsByExchangeName(String name) {
        Optional<Exchange> exchange = exchangeRepository.findByName(name);
        if(!exchange.isPresent()) return null;
        return accountRepository.findAllByExchange(exchange.get());
    }

    @Override
    public Account getAccountById(UUID id) {
        Optional<Account> optAccount = accountRepository.findById(id);
        if (optAccount.isPresent() && user.getAccountSet().contains(optAccount.get()))
            return optAccount.get();
        return null;
    }

    @Override
    public Set<Order> getOrderByAccount(UUID id) {
        Optional<Account> optAccount = accountRepository.findById(id);
        if (optAccount.isPresent()) {
            return optAccount.get().getOrderSet();
        }
        return null;
    }

    @Override
    public void setProfileIdDetails(UUID id, String profileId, String profileName) {
        Optional<Account> optAccount = accountRepository.findById(id);
        if(!optAccount.isPresent()) return;
        optAccount.get().setUserIdExchange(profileId);
        optAccount.get().setUserNameExchange(profileName);
        accountRepository.save(optAccount.get());
    }
}
