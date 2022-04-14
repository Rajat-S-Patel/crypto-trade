package com.pirimid.cryptotrade.bootstrap;

import com.pirimid.cryptotrade.helper.exchange.coinbase.ExcCoinbase;
import com.pirimid.cryptotrade.helper.exchange.gemini.ExcGemini;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.websocket.coinbase.WSCoinbase;
import com.pirimid.cryptotrade.websocket.gemini.WSGemini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
@Configuration
public class WebSocketBootsrap {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    WSCoinbase wsCoinbase;
    @Autowired
    WSGemini wsGemini;
    @Autowired
    ExcGemini excGemini;
    @Autowired
    ExcCoinbase excCoinbase;
    private void connecToCoinbase(){
        wsCoinbase.connect();
    }
    private void connecToGemini(){
        wsGemini.connect();
    }
    @EventListener(ApplicationReadyEvent.class)
    public void run() throws Exception {
        connecToCoinbase();
        connecToGemini();
        excGemini.getPairs();
        excCoinbase.getPairs();
    }
}
