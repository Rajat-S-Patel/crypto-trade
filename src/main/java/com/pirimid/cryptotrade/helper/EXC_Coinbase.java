package com.pirimid.cryptotrade.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Component
public class EXC_Coinbase implements EXC_Parent{
    @Value("${api.exchange.coinbase.baseurl}")
    private String baseUrl;
    private static String secretKeyString="QSbSkFaeoy7yb/yIwPSoQDom8LbYQR23ESAu3CXpnchZy5AKKJEb15bBuZ0jUvUk5Up/pEY5L8cect7arDz70A==";

    private ResponseEntity<String> apiCaller(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
//                .setHeader("X-API-KEY",key)
                .setHeader("Content-Type","application/json")
                .method("GET",HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(response.body(),headers, HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<String> getPairs(){
        try {
            return apiCaller(baseUrl+"/products");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> accountInfo(String apiKey, String secretKey, String passphrase, String timestamp) {
        return null;
    }

    @Override
    public ResponseEntity<String> createOrder(String apiKey, String secretKey, String passphrase, String timestamp, Map<String, String> body) {
        return null;
    }


}
