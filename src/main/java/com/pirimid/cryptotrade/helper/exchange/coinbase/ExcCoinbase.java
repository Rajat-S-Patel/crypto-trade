package com.pirimid.cryptotrade.helper.exchange.coinbase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.exception.OrderFailedException;
import com.pirimid.cryptotrade.helper.exchange.ExcParent;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.request.PlaceOrderReqCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.ErrorMessage;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.PlaceOrderResCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.SymbolResCoinbaseDTO;
import com.pirimid.cryptotrade.util.CoinbaseUtil;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcCoinbase implements ExcParent {
    @Value("${api.exchange.coinbase.baseurl}")
    private String baseUrl;
    private List<SymbolResDTO> symbolsDetails = new ArrayList<>();
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
        if(symbolsDetails!=null & symbolsDetails.size()>0){
            return  symbolsDetails;
        }
        List<SymbolResCoinbaseDTO> symbolResCoinbaseDTOS;
        try {
            ResponseEntity<String> response = apiCallerOpen(baseUrl + "/products");
            symbolResCoinbaseDTOS = new ObjectMapper().readValue(response.getBody(), new TypeReference<List<SymbolResCoinbaseDTO>>() {});
            symbolsDetails = CoinbaseUtil.getPairsResDTO(symbolResCoinbaseDTOS);
            return symbolsDetails;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> accountInfo(String apiKey, String secretKey, String passphrase) {

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature;
        try {
            signature = CoinbaseUtil.getSignature(timestamp, secretKey, "GET", "/profiles", "");
            return apiCallerRestricted(baseUrl + "/profiles", "GET", apiKey, passphrase, signature, timestamp, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public OrderResDTO createOrder(String apiKey, String secretKey, String passphrase, PlaceOrderReqDTO placeOrderReqDTO) {
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature;
        PlaceOrderReqCoinbaseDTO placeOrderReqCoinbaseDTO = CoinbaseUtil.getPlaceOrderReqDTO(placeOrderReqDTO);
        PlaceOrderResCoinbaseDTO placeOrderResCoinbaseDTO;
        try {
            String b =  new ObjectMapper().writeValueAsString(placeOrderReqCoinbaseDTO);
            signature = CoinbaseUtil.getSignature(timestamp, secretKey, "POST", "/orders", b);
            ResponseEntity<String> response = apiCallerRestricted(baseUrl + "/orders", "POST", apiKey, passphrase, signature, timestamp, b);
            String responseBody = response.getBody().toString();
            if(response.getStatusCode() == HttpStatus.BAD_REQUEST ){
                ErrorMessage errorMessage = new ObjectMapper().readValue(responseBody, ErrorMessage.class);
                throw new OrderFailedException(errorMessage.getMessage());
            }
            placeOrderResCoinbaseDTO = new ObjectMapper().readValue(responseBody, PlaceOrderResCoinbaseDTO.class);
            OrderResDTO orderResDTO = CoinbaseUtil.getPlaceOrderResDTO(placeOrderResCoinbaseDTO);
            return orderResDTO;
        } catch (OrderFailedException e){
            throw e;
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
        ////// Coinbase will return orderId if order is canceled successfully else will return {"message":"message "}
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
