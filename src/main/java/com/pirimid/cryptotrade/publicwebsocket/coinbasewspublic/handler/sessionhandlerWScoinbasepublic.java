package com.pirimid.cryptotrade.publicwebsocket.coinbasewspublic.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.publicwebsocket.coinbasewspublic.CoinbaseWSpublic;
import com.pirimid.cryptotrade.publicwebsocket.coinbasewspublic.dto.TickerCoinbaseDto;
import com.pirimid.cryptotrade.websocket.coinbase.req.ChannelReq;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqChannel;
import com.pirimid.cryptotrade.websocket.coinbase.req.ReqType;
import com.pirimid.cryptotrade.websocket.coinbase.res.Restype;
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
import java.util.List;

public class sessionhandlerWScoinbasepublic implements WebSocketHandler {
    private Boolean isConnected = false;
    private WebSocketSession session;
    private CoinbaseWSpublic coinbaseWSpublic;
    public sessionhandlerWScoinbasepublic(CoinbaseWSpublic coinbaseWSpublic) {
        this.coinbaseWSpublic = coinbaseWSpublic;
    }

    private void sendData(String data) throws IOException {
        WebSocketMessage webSocketMessage = new TextMessage(data);
        session.sendMessage(webSocketMessage);
    }
    private void tickerchannel() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Long dateInL = Instant.now().getEpochSecond();
        List<ReqChannel> channels = new ArrayList<>();
        channels.add(ReqChannel.HEARTBEAT);
        channels.add(ReqChannel.TICKER);
        List<String> pids = new ArrayList<>();
        pids.add("BTC-USD");
        ChannelReq req = ChannelReq.builder()
                .type(ReqType.SUBSCRIBE)
                .productIds(pids)
                .channels(channels)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(req);
        sendData(data);
        System.out.println("connentefysdysvd");
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        try {
            tickerchannel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message.getPayload().toString().contains("{\"type\":\"heartbeat\"")) {
            return;
        }
        TickerCoinbaseDto tickerCoinbaseDto = new ObjectMapper().readValue(message.getPayload().toString(), TickerCoinbaseDto.class);
        if (Restype.valueOf(tickerCoinbaseDto.getType().toUpperCase()) == Restype.SUBSCRIPTIONS) {
            isConnected = true;
            return;
        }
        if (isConnected) {
            if(Restype.valueOf(tickerCoinbaseDto.getType().toUpperCase()) == Restype.TICKER){
                new CoinbaseWsPublicHelper().emitData(tickerCoinbaseDto);
            }
        }


    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        isConnected = false;
        this.coinbaseWSpublic.connect();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
