package com.pirimid.cryptotrade.helper.exchange.coinbase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderResDTO;

import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.request.PlaceOrderReqCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.PlaceOrderResCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.SymbolResCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.ExcParent;
import com.pirimid.cryptotrade.util.CoinbaseUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

@Component
public class ExcCoinbase implements ExcParent {
    @Value("${api.exchange.coinbase.baseurl}")
    private String baseUrl;

    private ResponseEntity<String> apiCallerOpen(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader("Content-Type", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(response.body(), headers, response.statusCode());
    }

    private ResponseEntity<String> apiCallerRestricted(String uri, String method, String apiKey, String passphrase, String signature, String timestamp, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader("Content-Type", "application/json")
                .setHeader("CB-ACCESS-KEY", apiKey)
                .setHeader("CB-ACCESS-PASSPHRASE", passphrase)
                .setHeader("CB-ACCESS-SIGN", signature)
                .setHeader("CB-ACCESS-TIMESTAMP", timestamp)
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(response.body(), headers, response.statusCode());
    }

    @Override
    public List<SymbolResDTO> getPairs() {
        SymbolResCoinbaseDTO symbolResCoinbaseDTO;
        Gson gson = new Gson();
        List<SymbolResCoinbaseDTO> symbolResCoinbaseDTOS;
        List<SymbolResDTO> symbolResDTOS;
        try {
            ResponseEntity<String> response = apiCallerOpen(baseUrl + "/products");
            Type SymbolListType = new TypeToken<List<SymbolResCoinbaseDTO>>() {
            }.getType();
            symbolResCoinbaseDTOS = gson.fromJson(response.getBody(), SymbolListType);
            symbolResDTOS = CoinbaseUtil.getPairsResDTO(symbolResCoinbaseDTOS);
            return symbolResDTOS;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> accountInfo(String apiKey, String secretKey, String passphrase) {

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature;
        try {
            signature = CoinbaseUtil.getSignature(timestamp, secretKey, "GET", "/accounts", "");
            return apiCallerRestricted(baseUrl + "/accounts", "GET", apiKey, passphrase, signature, timestamp, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PlaceOrderResDTO createOrder(String apiKey, String secretKey, String passphrase, PlaceOrderReqDTO placeOrderReqDTO) {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature;
        Gson gson = new Gson();
        PlaceOrderReqCoinbaseDTO placeOrderReqCoinbaseDTO = CoinbaseUtil.getPlaceOrderReqDTO(placeOrderReqDTO);
        PlaceOrderResCoinbaseDTO placeOrderResCoinbaseDTO;
        try {
            String b = gson.toJson(placeOrderReqCoinbaseDTO);
            signature = CoinbaseUtil.getSignature(timestamp, secretKey, "POST", "/orders", b);
            ResponseEntity<String> response = apiCallerRestricted(baseUrl + "/orders", "POST", apiKey, passphrase, signature, timestamp, gson.toJson(placeOrderReqCoinbaseDTO));
            String se = response.getBody();
            placeOrderResCoinbaseDTO = gson.fromJson(se, PlaceOrderResCoinbaseDTO.class);
            PlaceOrderResDTO placeOrderResDTO = CoinbaseUtil.getPlaceOrderResDTO(placeOrderResCoinbaseDTO);
            return placeOrderResDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override
    public boolean cancelOrder(String apiKey, String secretKey, String passphrase, String orderId) {
        String timestamp = Instant.now().getEpochSecond() + "";
        String signature;
        int pos = -1;
        ////// Coinbase will return orderid if order is canceled successfully else will return {"message":"message "}
        try {
            signature = CoinbaseUtil.getSignature(timestamp, secretKey, "DELETE", "/orders/" + orderId, "");
            ResponseEntity<String> response = apiCallerRestricted(baseUrl + "/orders/" + orderId, "DELETE", apiKey, passphrase, signature, timestamp, "");
            String res = response.getBody();
            pos = res.indexOf("message");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pos == -1)
            return true;
        return false;

    }

}
