package com.pirimid.cryptotrade.bootstrap;

import com.pirimid.cryptotrade.service.ExchangeService;
import com.pirimid.cryptotrade.websocket.privateWS.coinbase.WSCoinbase;
import com.pirimid.cryptotrade.websocket.privateWS.gemini.WSGemini;
import com.pirimid.cryptotrade.websocket.publicWS.coinbase.CoinbaseWSpublic;
import com.pirimid.cryptotrade.websocket.publicWS.gemini.GeminiWSPublic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Configuration
public class WebSocketBootsrap {
    @Autowired
    WSCoinbase wsCoinbase;
    @Autowired
    WSGemini wsGemini;

    @Autowired
    CoinbaseWSpublic coinbaseWSpublic;
    @Autowired
    GeminiWSPublic geminiWSPublic;

    @Autowired
    ExchangeService exchangeService;

    private void connecToCoinbase(){
        wsCoinbase.connect();
    }
    private void connecToGemini(){
        wsGemini.connect();
    }
    @EventListener(ApplicationReadyEvent.class)
    public void run() throws Exception {

//        connecToGemini();
        exchangeService.fetchAllPairs();
        coinbaseWSpublic.connect();
        geminiWSPublic.connect();
        connecToCoinbase();
    }
}
