package com.pirimid.cryptotrade.bootstrap;

import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.AccountRepository;
import com.pirimid.cryptotrade.repository.UserRepository;
import com.pirimid.cryptotrade.websocket.coinbase.WSCoinbase;
import com.pirimid.cryptotrade.websocket.gemini.WSGemini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class WebSocketBootsrap {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    WSCoinbase wsCoinbase;
    @Autowired
    WSGemini wsGemini;
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
    }
}
