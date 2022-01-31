package com.pirimid.cryptotrade.websocket.coinbase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.coinbase.ExcCoinbase;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.ProfileResDTO;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.websocket.WSExchange;
import com.pirimid.cryptotrade.websocket.coinbase.handler.CoinbaseSessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class WSCoinbase implements WSExchange {
    @Value("${ws.exchange.coinbase.baseurl}")
    String baseUrl;
    @Autowired
    AccountService accountService;
    @Autowired
    OrderService orderService;
    @Autowired
    ExcCoinbase coinbase;

    @Override
    public void connect() {
        Set<Account> accounts = accountService.getAllAccountsByExchangeName(EXCHANGE.COINBASE);
        if (accounts == null || accounts.size() == 0) return;
        accounts
                .stream()
                .forEach(account -> {
                    if (account.getUserIdExchange() == null || account.getUserIdExchange().isEmpty() || account.getUserIdExchange().isBlank()) {
                        ResponseEntity<String> res = coinbase.accountInfo(account.getApiKey(), account.getSecretKey(), account.getPassPhrase());
                        String json = res.getBody();
                        Type profileListType = new TypeToken<List<ProfileResDTO>>() {
                        }.getType();
                        List<ProfileResDTO> profiles = new Gson().fromJson(json, profileListType);
                        if (profiles != null && profiles.size() > 0) {
                            ProfileResDTO profile = profiles.get(0);
                            accountService.setProfileIdDetails(account.getAccountId(), profile.getId().toString(), profile.getName());
                        }
                    }
                    try {
                        WebSocketClient client = new StandardWebSocketClient();
                        client.doHandshake(new CoinbaseSessionHandler(account, orderService), new WebSocketHttpHeaders(), URI.create(baseUrl)).get(10000, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                });
        return;
    }
}

