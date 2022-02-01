package com.pirimid.cryptotrade.helper.exchange;

import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ExcParent {

    List<SymbolResDTO> getPairs();

    ResponseEntity<String> accountInfo(String apiKey, String secretKey, String passphrase);

    OrderResDTO createOrder(String apiKey, String secretKey, String passphrase, PlaceOrderReqDTO req);

    boolean cancelOrder(String apiKey, String secretKey, String passphrase, String orderId);
}
