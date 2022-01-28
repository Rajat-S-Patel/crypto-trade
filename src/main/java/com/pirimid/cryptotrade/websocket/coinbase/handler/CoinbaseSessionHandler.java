package com.pirimid.cryptotrade.websocket.coinbase.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.coinbase.ExcCoinbase;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.ProfileResDTO;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.service.AccountService;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.CoinbaseUtil;
import com.pirimid.cryptotrade.websocket.coinbase.req.ChannelReq;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqChannel;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqType;
import com.pirimid.cryptotrade.websocket.coinbase.res.Typedto;
import com.pirimid.cryptotrade.websocket.coinbase.res.WSCoinbaseOrderDto;
import com.pirimid.cryptotrade.websocket.coinbase.res.WsCoinbaseTradeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class CoinbaseSessionHandler implements WebSocketHandler {

    private AccountService accountService;
    private OrderService orderService;
    private ExcCoinbase coinbase;
    private WebSocketSession session;
    Gson gson = new Gson();
    public CoinbaseSessionHandler(AccountService accountService, OrderService orderService,ExcCoinbase coinbase){
        this.accountService = accountService;
        this.orderService = orderService;
        this.coinbase = coinbase;
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
                    if(account.getUserIdExchange()==null || account.getUserIdExchange().isEmpty() || account.getUserIdExchange().isBlank()){
                        ResponseEntity<String> res = coinbase.accountInfo(account.getApiKey(),account.getSecretKey(),account.getPassPhrase());
                        String json = res.getBody();
                        System.out.println(json);
                        Type profileListType = new TypeToken<List<ProfileResDTO>>(){}.getType();
                        List<ProfileResDTO> profiles = new Gson().fromJson(json,profileListType);
                        if(profiles!=null && profiles.size()>0) {
                            ProfileResDTO profile = profiles.get(0);
                            accountService.setProfileIdDetails(account.getAccountId(), profile.getId().toString(), profile.getName());
                            System.out.println("Adding profile - " +profile.getId()+" "+profile.getName()+" of account -"+account.getAccountId());
                        }
                    }
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
        if(message.getPayload().toString().contains("heartbeat"))
            return;
        Typedto typedto = gson.fromJson(message.getPayload().toString(),Typedto.class);
        System.out.println("##");
        System.out.println(message.getPayload().toString());

        switch (typedto.getType()){

            case "received":{
                WSCoinbaseOrderDto wsCoinbaseOrderDto = gson.fromJson(message.getPayload().toString(),WSCoinbaseOrderDto.class);
                OrderResDTO orderResDTO = CoinbaseUtil.getWsPlaceOrderResDTO(wsCoinbaseOrderDto);
                System.out.println(orderResDTO.toString());
                //call method for order received
                orderService.createOrder(orderResDTO,"coinbase");
                break;
            }
            case "done":{

                WSCoinbaseOrderDto wsCoinbaseOrderDto = gson.fromJson(message.getPayload().toString(),WSCoinbaseOrderDto.class);
                if(wsCoinbaseOrderDto.getReason().equals("filled")){
                    OrderResDTO orderResDTO = CoinbaseUtil.getWsPlaceOrderResDTO(wsCoinbaseOrderDto);
                    System.out.println(orderResDTO.toString());
                    ////call method for order closed(filled)
                    orderService.completeOrder(orderResDTO,"coinbase");
                }
                else if(wsCoinbaseOrderDto.getReason().equals("canceled")){
                    OrderResDTO orderResDTO = CoinbaseUtil.getWsPlaceOrderResDTO(wsCoinbaseOrderDto);
                    System.out.println(orderResDTO.toString());
                    //call method for order cancelled
                    orderService.cancelOrderByExchangeOrderId(orderResDTO.getExchangeOrderId(),"coinbase",orderResDTO.getEndAt());
                }
                break;
            }
            case "match":{
                WsCoinbaseTradeDto wsCoinbaseTradeDto = gson.fromJson(message.getPayload().toString(),WsCoinbaseTradeDto.class);
                TradeDto tradeDto = CoinbaseUtil.getWsTradeResDTO(wsCoinbaseTradeDto);
                System.out.println(tradeDto.toString());
                //call method for trade
                orderService.addTrade(tradeDto);
                break;
            }
        }
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
