package com.pirimid.cryptotrade.websocket;

//import org.springframework.web.socket.WebSocketSession;

import org.springframework.web.socket.WebSocketSession;

public interface WSExchange {
    WebSocketSession connect();
}
