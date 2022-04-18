package com.pirimid.cryptotrade.websocket.publicWS.gemini;

import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.util.GeminiUtil;
import com.pirimid.cryptotrade.websocket.clientWS.config.PublicWebsocketService;
import com.pirimid.cryptotrade.websocket.publicWS.WsTickerDto;
import com.pirimid.cryptotrade.websocket.publicWS.gemini.dto.TickerGeminiDto;
import com.pirimid.cryptotrade.websocket.publicWS.gemini.handler.GeminiPublicSessionHandler;
import com.pirimid.cryptotrade.websocket.publicWS.publicWS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Component
public class GeminiWSPublic implements publicWS {

    private Map<String,SymbolResDTO> symbolMap;

    @Value("wss://api.gemini.com/v1/multimarketdata?heartbeat=true&symbols=")
    private String baseUrl;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    private PublicWebsocketService websocketService;

    private List<WsTickerDto> normaliseData(TickerGeminiDto geminiDto){
        List<WsTickerDto> tickerDtoList = new ArrayList<>();
        geminiDto.getEvents().forEach(event->{
            WsTickerDto tickerDto = WsTickerDto.builder()
                    .price(event.getPrice())
                    .symbol(symbolMap.get(event.getSymbol().toLowerCase()).getSymbol())
                    .build();
            tickerDtoList.add(tickerDto);
        });
//        System.out.println(tickerDtoList);
        return tickerDtoList;
    }
    public void sendDataToChannel(TickerGeminiDto geminiDto){
        List<WsTickerDto> wsTickerDtoList =  normaliseData(geminiDto);
        wsTickerDtoList.forEach(wsTickerDto -> {
            String destination = "/topic/price.gemini."+wsTickerDto.getSymbol();
//            System.out.println(destination);
            if(websocketService.getSubscribedPairs().containsKey(destination)){
                messagingTemplate.convertAndSend(destination,wsTickerDto);
                System.out.println(destination);
            }
        });
    }
    @Override
    public void connect() {
        this.symbolMap = GeminiUtil.getPairs();
        if(symbolMap == null) {
            return;
        }
        String symbols = String.join(",",symbolMap.values().stream().map(p->p.getBase()+p.getQuote()).collect(Collectors.toList()));
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
