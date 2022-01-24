package com.pirimid.cryptotrade.websocket.coinbase.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.repository.UserRepository;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.util.CoinbaseUtil;
import com.pirimid.cryptotrade.websocket.coinbase.req.ChannelReq;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqChannel;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CoinbaseSessionHandler implements WebSocketHandler {

    private AccountService accountService;
    private WebSocketSession session;
    public CoinbaseSessionHandler(AccountService accountService){
        this.accountService = accountService;
    }
    private void sendData(String data) throws IOException {
        WebSocketMessage webSocketMessage =new TextMessage(data);
        session.sendMessage(webSocketMessage);
    }
    private void heartBeatChannel() throws IOException {
        List<ReqChannel> channels = new ArrayList<>();
        channels.add(ReqChannel.HEARTBEAT);
        List<String> pids = new ArrayList<>();
        pids.add("BTC-USD");
        ChannelReq req = ChannelReq.builder().type(ReqType.SUBSCRIBE).productIds(pids).channels(channels).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(req);
        sendData(data);
    }

    private void fullChannelWithAuth(String key,String secretKey,String passphrase) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Long dateInL = Instant.now().getEpochSecond();
        Date date = new Date(dateInL);
        String signature = CoinbaseUtil.getSignature(String.valueOf(dateInL),secretKey,"GET","/users/self/verify","");
        List<ReqChannel> channels = new ArrayList<>();
        channels.add(ReqChannel.USER);
        channels.add(ReqChannel.HEARTBEAT);
        List<String> pids = new ArrayList<>();
        pids.add("BTC-USD");
        ChannelReq req = ChannelReq.builder()
                .type(ReqType.SUBSCRIBE)
                .productIds(pids)
                .channels(channels)
                .key(key)
                .passphrase(passphrase)
                .signature(signature)
                .timestamp(date)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(req);
        System.out.println("fullChannelWithAuth");
        sendData(data);

    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("coinbase - connection established");
        this.session=session;
        Set<Account> accounts = accountService.getAllAccountsByExchangeName("coinbase");
//        heartBeatChannel();
        if(accounts==null || accounts.size()==0) return;
        accounts
                .stream()
                .forEach(account -> {
                    System.out.println("connecting to coinbase account"+account.getApiKey());
                    try {
                        fullChannelWithAuth(account.getApiKey(),account.getSecretKey(),account.getPassPhrase());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                });

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if(message.getPayload().toString().contains("heartbeat")) return;
        System.out.println("coinbase -");
        System.out.println(message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("coinbase -connection closed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
