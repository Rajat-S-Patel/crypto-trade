package com.pirimid.cryptotrade.websocket.gemini;

import org.springframework.web.socket.WebSocketSession;

public interface WSExchange {
    WebSocketSession connect();
}
