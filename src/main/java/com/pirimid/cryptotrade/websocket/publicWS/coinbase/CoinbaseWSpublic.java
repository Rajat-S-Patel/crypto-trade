package com.pirimid.cryptotrade.websocket.publicWS.coinbase;

import com.pirimid.cryptotrade.websocket.publicWS.WsTickerDto;
import com.pirimid.cryptotrade.websocket.publicWS.coinbase.dto.TickerCoinbaseDto;
import com.pirimid.cryptotrade.websocket.publicWS.coinbase.handler.sessionhandlerWScoinbasepublic;
import com.pirimid.cryptotrade.websocket.publicWS.publicWS;
import com.pirimid.cryptotrade.websocket.clientWS.config.PublicWebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    @Value("wss://ws-feed.exchange.coinbase.com")
    private String baseUrl;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PublicWebsocketService websocketService;


    private WsTickerDto normaliseData(TickerCoinbaseDto tickerCoinbaseDto){
        WsTickerDto wsTickerDto = WsTickerDto.builder()
                .symbol(tickerCoinbaseDto.getProductId())
                .price(tickerCoinbaseDto.getPrice())
                .high(tickerCoinbaseDto.getHigh24h())
                .low(tickerCoinbaseDto.getLow24h())
                .volume(tickerCoinbaseDto.getVolume24h())
                .percentChange(((tickerCoinbaseDto.getPrice()/tickerCoinbaseDto.getOpen24h())-1)*100)
                .build();
        return wsTickerDto;
    }
    public void sendDataToChannel(TickerCoinbaseDto tickerCoinbaseDto){
        WsTickerDto wsTickerDto = normaliseData(tickerCoinbaseDto);
        String destination = "/topic/price.coinbase."+tickerCoinbaseDto.getProductId();
        if(websocketService.getSubscribedPairs().containsKey(destination)) {
            messagingTemplate.convertAndSend(destination, wsTickerDto);
        }
    }

    @Override
    public void connect(){
        WebSocketClient client = new StandardWebSocketClient();
        try {
            client
                    .doHandshake(new sessionhandlerWScoinbasepublic(this), new WebSocketHttpHeaders(), URI.create(baseUrl))
                    .get(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
