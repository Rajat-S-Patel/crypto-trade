package com.pirimid.cryptotrade.controller;

import com.pirimid.cryptotrade.exception.AccountNotFoundException;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;
    @GetMapping("/accounts")    // accounts?exchange=
    public ResponseEntity<Set<Account>> getAccountsByExchange(@RequestParam String exchange){
        Set<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(accounts);
    }

    @GetMapping("/accounts/{accountId}/orders")
    public ResponseEntity<Set<Order>> getOrderByAccount(@PathVariable UUID accountId){
        Set<Order> orders = accountService.getOrderByAccount(accountId);
        if(orders == null || orders.isEmpty()) throw new AccountNotFoundException("No such account in database");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(orders);
    }
    @GetMapping("/account-info/{accountId}")
    public ResponseEntity<Account> getAccountInfo(@PathVariable UUID accountId){
        Account account = accountService.getAccountById(accountId);
        if(account==null) throw new AccountNotFoundException("No such account in database");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(account);
    }
}
