package com.pirimid.cryptotrade.websocket.publicWS.gemini;

import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.gemini.ExcGemini;
import com.pirimid.cryptotrade.websocket.publicWS.gemini.handler.GeminiPublicSessionHandler;
import com.pirimid.cryptotrade.websocket.publicWS.publicWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Component
public class GeminiWSPublic implements publicWS {
    @Value("wss://api.gemini.com/v1/multimarketdata?heartbeat=true&symbols=")
    private String baseUrl;
    @Autowired
    private ExcGemini excGemini;
    @Override
    public void connect() {
        List<SymbolResDTO> pairs = excGemini.getPairs();
        String symbols = String.join(",",pairs.stream().map(p->p.getBase()+p.getQuote()).collect(Collectors.toList()));
        WebSocketClient client = new StandardWebSocketClient();
        try {
            client.doHandshake(new GeminiPublicSessionHandler(this),new WebSocketHttpHeaders(), URI.create(baseUrl+symbols))
                    .get(10000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
