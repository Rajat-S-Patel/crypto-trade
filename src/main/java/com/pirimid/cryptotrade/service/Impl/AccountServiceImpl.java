package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.AccountRepository;
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
    User user;
    @Override
    public Set<Account> getAllAccounts() {
//        UUID userid=UUID.randomUUID();
//        Optional<User> optUser = userRepository.findById(userid);
//        if(optUser.isPresent()){
//            return optUser.get().getAccountSet();
//        }
        return user.getAccountSet();
    }

    @Override
    public Account getAccountById(UUID id) {
//        UUID userid=UUID.randomUUID();
//        Optional<User> optUser = userRepository.findById(userid);
        Optional<Account> optAccount = accountRepository.findById(id);
//        if(optUser.isPresent() && optAccount.isPresent() && optUser.get().getAccountSet().contains(optAccount.get())){
//            return optAccount.get();
//        }
        if(optAccount.isPresent() && user.getAccountSet().contains(optAccount.get()))
            return optAccount.get();
        return null;
    }

    @Override
    public Set<Order> getOrderByAccount(UUID id) {
        Optional<Account> optAccount = accountRepository.findById(id);
        if(optAccount.isPresent()){
            return optAccount.get().getOrderSet();
        }
        return null;
    }
}
