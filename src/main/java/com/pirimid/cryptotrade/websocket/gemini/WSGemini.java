package com.pirimid.cryptotrade.websocket.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.gemini.ExcGemini;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.GeminiUtil;
import com.pirimid.cryptotrade.websocket.WSExchange;
import com.pirimid.cryptotrade.websocket.gemini.handler.GeminiSessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
@Component
public class WSGemini implements WSExchange {
    @Autowired
    ExcGemini gemini;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OrderService orderService;
    @Value("${ws.exchange.gemini.baseurl}")
    String baseurl;
    private void establishConnection(Account account) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException, TimeoutException {
        Map<String,String> payload = new HashMap<>();
        payload.put("request","/v1/order/events");
        payload.put("nonce",String.valueOf(GeminiUtil.getNonce()));
        String json = new ObjectMapper().writeValueAsString(payload);
        byte[] b64 = GeminiUtil.getB64(json);
        String signature = GeminiUtil.getSignature(b64,account.getSecretKey());

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("X-GEMINI-APIKEY",account.getApiKey());
        headers.add("X-GEMINI-PAYLOAD",new String(b64, StandardCharsets.UTF_8));
        headers.add("X-GEMINI-SIGNATURE",signature);
        client.doHandshake(new GeminiSessionHandler(account,orderService),headers, URI.create(baseurl)).get(10000, TimeUnit.SECONDS);
    }
    @Override
    public void connect() {
        Set<Account> accounts = accountService.getAllAccountsByExchangeName(EXCHANGE.GEMINI);
        if(accounts==null || accounts.size()==0) return;
        accounts
                .stream()
                .forEach(account -> {
                    try {
                        establishConnection(account);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                });
    }
}
