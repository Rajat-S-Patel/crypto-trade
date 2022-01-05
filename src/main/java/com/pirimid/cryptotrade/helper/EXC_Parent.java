package com.pirimid.cryptotrade.helper;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface EXC_Parent {
    ResponseEntity<String> getPairs();
    ResponseEntity<String> accountInfo(String apiKey,String secretKey,String passphrase,String timestamp);
    ResponseEntity<String> createOrder(String apiKey, String secretKey, String passphrase, String timestamp, Map<String,String> body);
}
