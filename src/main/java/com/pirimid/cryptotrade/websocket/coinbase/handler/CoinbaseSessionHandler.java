package com.pirimid.cryptotrade.websocket.coinbase.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.CoinbaseUtil;
import com.pirimid.cryptotrade.websocket.coinbase.WSCoinbase;
import com.pirimid.cryptotrade.websocket.coinbase.req.ChannelReq;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqChannel;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqType;
import com.pirimid.cryptotrade.websocket.coinbase.res.Reason;
import com.pirimid.cryptotrade.websocket.coinbase.res.Restype;
import com.pirimid.cryptotrade.websocket.coinbase.res.Typedto;
import com.pirimid.cryptotrade.websocket.coinbase.res.WSCoinbaseOrderDto;
import com.pirimid.cryptotrade.websocket.coinbase.res.WSCoinbaseTradeDto;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CoinbaseSessionHandler implements WebSocketHandler {

    private Account account;
    private OrderService orderService;
    private Boolean isConnected = false;
    private WebSocketSession session;
    private WSCoinbase coinbase;
    Gson gson = new Gson();


    public CoinbaseSessionHandler(Account account, OrderService orderService, WSCoinbase coinbase) {
        this.account = account;
        this.orderService = orderService;
        this.coinbase=coinbase;
    }

    private void sendData(String data) throws IOException {
        WebSocketMessage webSocketMessage = new TextMessage(data);
        session.sendMessage(webSocketMessage);
    }

    private void fullChannelWithAuth(String key, String secretKey, String passphrase) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Long dateInL = Instant.now().getEpochSecond();
        Date date = new Date(dateInL);
        String signature = CoinbaseUtil.getSignature(String.valueOf(dateInL), secretKey, "GET", "/users/self/verify", "");
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
        ChannelReq req1 = new ChannelReq();

        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(req);
        sendData(data);

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        try {
            fullChannelWithAuth(account.getApiKey(), account.getSecretKey(), account.getPassPhrase());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message.getPayload().toString().contains("{\"type\":\"heartbeat\""))
            return;
        Typedto typedto = gson.fromJson(message.getPayload().toString(), Typedto.class);
        if (Restype.valueOf(typedto.getType().toUpperCase()) == Restype.SUBSCRIPTIONS) {
            isConnected = true;
            return;
        }
        if (isConnected) {


            switch (Restype.valueOf(typedto.getType().toUpperCase())) {
                case RECEIVED: {
                    WSCoinbaseOrderDto wsCoinbaseOrderDto = gson.fromJson(message.getPayload().toString(), WSCoinbaseOrderDto.class);
                    OrderResDTO orderResDTO = CoinbaseUtil.getWsPlaceOrderResDTO(wsCoinbaseOrderDto);
                    orderResDTO.setAccountId(account.getAccountId());
                    //call method for order received
                    orderService.createOrder(orderResDTO);
                    break;
                }
                case DONE: {

                    WSCoinbaseOrderDto wsCoinbaseOrderDto = gson.fromJson(message.getPayload().toString(), WSCoinbaseOrderDto.class);
                    if (Reason.valueOf(wsCoinbaseOrderDto.getReason().toUpperCase()) == Reason.FILLED) {
                        OrderResDTO orderResDTO = CoinbaseUtil.getWsPlaceOrderResDTO(wsCoinbaseOrderDto);
                        orderResDTO.setAccountId(account.getAccountId());
                        ////call method for order closed(filled)
                        orderService.completeOrder(orderResDTO);
                    } else if (Reason.valueOf(wsCoinbaseOrderDto.getReason().toUpperCase()) == Reason.CANCELED) {
                        OrderResDTO orderResDTO = CoinbaseUtil.getWsPlaceOrderResDTO(wsCoinbaseOrderDto);
                        orderResDTO.setAccountId(account.getAccountId());
                        //call method for order cancelled
                        orderService.cancelOrderByExchangeOrderId(orderResDTO.getExchangeOrderId(), EXCHANGE.COINBASE, orderResDTO.getEndAt());
                    }
                    break;
                }
                case MATCH: {
                    WSCoinbaseTradeDto wsCoinbaseTradeDto = gson.fromJson(message.getPayload().toString(), WSCoinbaseTradeDto.class);
                    TradeDto tradeDto = CoinbaseUtil.getWsTradeResDTO(wsCoinbaseTradeDto);
                    //call method for trade
                    orderService.addTrade(tradeDto, EXCHANGE.COINBASE);
                    break;
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        isConnected = false;
        coinbase.establishConnection(account);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
