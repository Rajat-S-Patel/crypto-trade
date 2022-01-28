package com.pirimid.cryptotrade.websocket.coinbase;

import com.pirimid.cryptotrade.helper.exchange.coinbase.ExcCoinbase;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.websocket.WSExchange;
import com.pirimid.cryptotrade.websocket.coinbase.handler.CoinbaseSessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.Scanner;
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
    public WebSocketSession connect() {
        WebSocketClient client = new StandardWebSocketClient();
        try {
            client.doHandshake(new CoinbaseSessionHandler(accountService,orderService,coinbase),new WebSocketHttpHeaders(), URI.create(baseUrl)).get(10000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}
