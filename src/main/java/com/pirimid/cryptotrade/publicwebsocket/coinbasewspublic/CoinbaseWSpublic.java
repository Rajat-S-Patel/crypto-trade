package com.pirimid.cryptotrade.publicwebsocket.coinbasewspublic;

import com.pirimid.cryptotrade.publicwebsocket.coinbasewspublic.handler.sessionhandlerWScoinbasepublic;
import com.pirimid.cryptotrade.publicwebsocket.publicWS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class CoinbaseWSpublic implements publicWS {
    @Value("${ws.exchange.coinbase.baseurl}")
    private String baseUrl;
    @Override
    public void connect() throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketClient client = new StandardWebSocketClient();
        client.doHandshake(new sessionhandlerWScoinbasepublic(this), new WebSocketHttpHeaders(), URI.create(baseUrl)).get(10000, TimeUnit.SECONDS);
    }
}
