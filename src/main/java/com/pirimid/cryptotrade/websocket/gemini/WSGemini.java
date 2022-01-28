package com.pirimid.cryptotrade.websocket.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.ProfileResDTO;
import com.pirimid.cryptotrade.helper.exchange.gemini.ExcGemini;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.GeminiUtil;
import com.pirimid.cryptotrade.websocket.WSExchange;
import com.pirimid.cryptotrade.websocket.gemini.handler.GeminiSessionHandler;
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
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
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
    private void establishConnection(String key,String secretKey) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeyException, ExecutionException, InterruptedException, TimeoutException {
        Map<String,String> payload = new HashMap<>();
        payload.put("request","/v1/order/events");
        payload.put("nonce",String.valueOf(GeminiUtil.getNonce()));
        String json = new ObjectMapper().writeValueAsString(payload);
        byte[] b64 = GeminiUtil.getB64(json);
        String signature = GeminiUtil.getSignature(b64,secretKey);

        WebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("X-GEMINI-APIKEY",key);
        headers.add("X-GEMINI-PAYLOAD",new String(b64, StandardCharsets.UTF_8));
        headers.add("X-GEMINI-SIGNATURE",signature);
        client.doHandshake(new GeminiSessionHandler(orderService),headers, URI.create(baseurl)).get(10000, TimeUnit.SECONDS);
    }
    @Override
    public WebSocketSession connect() {
        Set<Account> accounts = accountService.getAllAccountsByExchangeName("gemini");
        if(accounts==null || accounts.size()==0) return null;
        accounts
                .stream()
                .forEach(account -> {
                    if(account.getUserIdExchange()==null || account.getUserIdExchange().isEmpty() || account.getUserIdExchange().isBlank()){
                        ResponseEntity<String> res = gemini.accountInfo(account.getApiKey(),account.getSecretKey(),account.getPassPhrase());

//                        String json = res.getBody();
//                        System.out.println(json);
//                        Type profileListType = new TypeToken<List<ProfileResDTO>>(){}.getType();
//                        List<ProfileResDTO> profiles = new Gson().fromJson(json,profileListType);
//                        if(profiles!=null && profiles.size()>0) {
//                            ProfileResDTO profile = profiles.get(0);
//                            accountService.setProfileIdDetails(account.getAccountId(), profile.getId().toString(), profile.getName());
//                            System.out.println("Adding profile - " +profile.getId()+" "+profile.getName()+" of account -"+account.getAccountId());
//                        }
                    }
                    System.out.println("connecting to gemini account"+account.getApiKey());
                    try {
                        establishConnection(account.getApiKey(),account.getSecretKey());
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
        return null;
    }
}
