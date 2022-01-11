package com.pirimid.cryptotrade.helper.exchange.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderResDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.EXC_Parent;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.request.CreateOrderRequest;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CancelOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CreateOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.SymbolResponse;
import com.pirimid.cryptotrade.util.GeminiUtil;
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
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class EXC_Gemini implements EXC_Parent {

    @Value("${api.exchange.gemini.baseurl}")
    private String baseUrl;
    private long getNonce(){
        return new Date().getTime();
    }
    private byte[] getB64(String payload){
        return Base64.getEncoder().encode(payload.getBytes(StandardCharsets.UTF_8));
    }
    private ResponseEntity<String> apiCaller(String uri,String reqType) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader("X-GEMINI-APIKEY","master-sIFFhvWpYBof8QH2XECh")
                .setHeader("Content-Type","text/plain")
                .method(reqType,HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(response.body(),headers, HttpStatus.ACCEPTED);
    }

    private ResponseEntity<String> apiCaller(String uri,String reqType,byte[] b64,String signature,String apiKey) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .setHeader("X-GEMINI-APIKEY",apiKey)
                .setHeader("Content-Type","text/plain")
                .setHeader("X-GEMINI-PAYLOAD",new String(b64,StandardCharsets.UTF_8))
                .setHeader("X-GEMINI-SIGNATURE",signature)
                .setHeader("Cache-Control","no-cache")
                .method(reqType,HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request,HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(response.body(),headers, HttpStatus.ACCEPTED);
    }

    @Override
    public List<SymbolResDTO> getPairs() {
        List<SymbolResDTO> symbolDetails = new ArrayList<>();
        try {
            ResponseEntity<String> res = apiCaller(baseUrl+"/v1/symbols","GET");
            List<String> symbols= new ObjectMapper().readValue(res.getBody(), new TypeReference<List<String>>() {});
            for(String symbol : symbols){
                ResponseEntity<String> symRes = apiCaller(baseUrl+"/v1/symbols/details/"+symbol,"GET");
                SymbolResponse response = new ObjectMapper().readValue(symRes.getBody(),SymbolResponse.class);
                System.out.println("response:- "+response);
                symbolDetails.add(GeminiUtil.getSymbolResDTO(response));
            }
            return symbolDetails;
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
        public PlaceOrderResDTO createOrder(String apiKey, String secretKey, String passphrase, PlaceOrderReqDTO body) {
        CreateOrderRequest request = GeminiUtil.getCreateOrderReqDTO(body);
        request.setClientOrderId("12344545");
        request.setNonce(getNonce());
        request.setAccountType("primary");
        request.setRequest("/v1/order/new");

        try {
            String json = new ObjectMapper().writeValueAsString(request);
            byte[] b64 = getB64(json);
            String signature = GeminiUtil.getSignature(b64,secretKey);
            ResponseEntity<String> res = apiCaller(baseUrl+"/v1/order/new","POST",b64,signature,apiKey);

            CreateOrderResponse orderResponse = new ObjectMapper().readValue(res.getBody(), CreateOrderResponse.class);
            System.out.println("orderResponse: "+orderResponse);
            return GeminiUtil.getPlaceOrderResDTO(orderResponse);
        }
        catch (NoSuchAlgorithmException | InvalidKeyException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public boolean cancelOrder(String apiKey, String secretKey, String passphrase, String orderId) {
        final String url = "/v1/order/cancel";
        Map<String,String> payload = new HashMap<>();
        payload.put("order_id",orderId);
        payload.put("nonce",String.valueOf(getNonce()));
        payload.put("account","primary");
        payload.put("request",url);
        try {
            String json = new ObjectMapper().writeValueAsString(payload);
            byte b64[] = Base64.getEncoder().encode(json.getBytes(StandardCharsets.UTF_8));
            String signature = GeminiUtil.getSignature(b64,secretKey);
            ResponseEntity<String> res  = apiCaller(baseUrl+url,"POST",b64,signature,apiKey);
            System.out.println("cancel-order:- "+res.getBody());
            CancelOrderResponse response = new ObjectMapper().readValue(res.getBody(),CancelOrderResponse.class);

            return response.isCancelled();

        } catch (JsonProcessingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

//    public ResponseEntity<String> getOrder(String apiKey, String secretKey, String passPhrase) {
//        try {
//            Map<String,String> mp= new HashMap<>();
//
//            mp.put("nonce",String.valueOf(getNonce()));
//            mp.put("request","/v1/order/status");
//            mp.put("order_id","1407777263");
//            mp.put("account","primary");
//            mp.put("include_trades","True");
//
//            String json = new ObjectMapper().writeValueAsString(mp);
//            System.out.println("json: "+json);
//            byte[] b64 = Base64.getEncoder().encode(json.getBytes(StandardCharsets.UTF_8));
//            String signature = GeminiUtil.getSignature(b64,secretKey);
//
//            return apiCaller(baseUrl+"/v1/order/status","POST",b64,signature,apiKey);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException | NoSuchAlgorithmException | InvalidKeyException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    @Override
//    public ResponseEntity<String> getAllOrders(String apiKey, String secretKey, String passPhrase) {
//        try {
//            Map<String,String> mp= new HashMap<>();
//
//            mp.put("nonce",String.valueOf(getNonce()));
//            mp.put("request","/v1/orders");
//            mp.put("account","primary");
//
//
//            String json = new ObjectMapper().writeValueAsString(mp);
//            System.out.println("json: "+json);
//            byte[] b64 = Base64.getEncoder().encode(json.getBytes(StandardCharsets.UTF_8));
//            String signature = GeminiUtil.getSignature(b64,secretKey);
//
//            return apiCaller(baseUrl+"/v1/orders","POST",b64,signature,apiKey);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException | NoSuchAlgorithmException | InvalidKeyException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
