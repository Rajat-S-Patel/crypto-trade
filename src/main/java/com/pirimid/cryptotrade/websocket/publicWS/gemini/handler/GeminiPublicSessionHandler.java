package com.pirimid.cryptotrade.websocket.publicWS.gemini.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.websocket.publicWS.gemini.GeminiWSPublic;
import com.pirimid.cryptotrade.websocket.publicWS.gemini.dto.TickerGeminiDto;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.List;

public class GeminiPublicSessionHandler implements WebSocketHandler {
    private GeminiWSPublic geminiWSPublic;
    public GeminiPublicSessionHandler(GeminiWSPublic geminiWSPublic){
        this.geminiWSPublic = geminiWSPublic;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setTextMessageSizeLimit(1024 * 1024 * 2);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if(message.getPayload().toString().contains("heartbeat")) return;
        TickerGeminiDto tickerGeminiDto = (new ObjectMapper()).readValue(message.getPayload().toString(),TickerGeminiDto.class);
        if(tickerGeminiDto.getType().equals("update"))
            this.geminiWSPublic.sendDataToChannel(tickerGeminiDto);

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        this.geminiWSPublic.connect();
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
