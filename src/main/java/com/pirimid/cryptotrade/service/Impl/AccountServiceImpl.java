package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.DTO.AccountDTO;
import com.pirimid.cryptotrade.DTO.BalanceDTO;
import com.pirimid.cryptotrade.exception.AccountAlreadyExistException;
import com.pirimid.cryptotrade.exception.InvalidApiKeyException;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.repository.ExchangeRepository;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.service.UserService;
import com.pirimid.cryptotrade.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExchangeRepository exchangeRepository;
    @Autowired
    UserService userService;
    @Autowired
    ExchangeUtil exchangeUtil;
    User user;
    @PostConstruct
    private void postConstructor(){
        user = userService.getDefaultUser();
    }
    @Override
    public Set<Account> getAllAccounts() {
        return user.getAccountSet();
    }

    @Override
    public Set<Account> getAllAccountsByExchangeName(EXCHANGE name) {
        Optional<Exchange> exchange = exchangeRepository.findByName(name.getValue().toLowerCase());
        if(!exchange.isPresent()) return null;
        return accountRepository.findAllByExchange(exchange.get());
    }

    @Override
    public Account getAccountById(UUID id) {
        Optional<Account> optAccount = accountRepository.findById(id);
        // TODO ask vishwas for user account related checks
        return optAccount.isPresent()?optAccount.get():null;
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
    public AccountDTO addAccount(AccountDTO accountDTO) {
        Account account = accountRepository.findAccountByUser_UserIdAndExchange_ExchangeId(accountDTO.getUserId(),accountDTO.getExchange().getExchangeId());
        if(account != null) throw new AccountAlreadyExistException("This account already exist from some other users id");
        Account newAccount = Account.builder()
                .accountLabel(accountDTO.getAccountLabel())
                .passPhrase(accountDTO.getPassPhrase())
                .apiKey(accountDTO.getApiKey())
                .secretKey(accountDTO.getSecretKey())
                .exchange(accountDTO.getExchange())
                .user(userService.getUserById(accountDTO.getUserId()))
                .build();
        List<BalanceDTO> balanceDTOS = exchangeUtil
                .getObject(EXCHANGE.valueOf(newAccount.getExchange().getName().toUpperCase()))
                .getBalance(newAccount.getApiKey(), newAccount.getSecretKey(), newAccount.getPassPhrase());
        if(balanceDTOS == null) throw new InvalidApiKeyException("Invalid API, Secret Key or passphrase");
        accountRepository.save(newAccount);
        return getAccountDTO(newAccount);
    }


    @Override
    public void setProfileIdDetails(UUID id, String profileId, String profileName) {
        Optional<Account> optAccount = accountRepository.findById(id);
        if(!optAccount.isPresent()) return;
        optAccount.get().setUserIdExchange(profileId);
        optAccount.get().setUserNameExchange(profileName);
        accountRepository.save(optAccount.get());
    }

    @Override
    public List<AccountDTO> getAccountsByUser(UUID userId) {
        // TODO add check for user existence
        Set<Account> accounts = accountRepository.findByUser(userService.getUserById(userId)).get();
        List<AccountDTO> accountDTOList = new ArrayList<>();
        accounts.stream().forEach(account -> {
            accountDTOList.add(getAccountDTO(account));
        });
        return accountDTOList;
    }

    @Override
    public void deleteAccountById(UUID accountId) {
        accountRepository.deleteById(accountId);
    }
    private AccountDTO getAccountDTO(Account account) {
        AccountDTO dto = AccountDTO.builder()
                .accountId(account.getAccountId())
                .exchange(account.getExchange())
                .apiKey(account.getApiKey())
                .accountLabel(account.getAccountLabel())
                .build();
        return dto;
    }

}
