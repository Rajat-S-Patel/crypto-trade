package com.pirimid.cryptotrade.helper.exchange.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.DTO.BalanceDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.ExcParent;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.request.CreateOrderRequest;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.BalanceGeminiDTO;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CancelOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CreateOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.PriceFeedRes;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExcGemini implements ExcParent {

    @Value("${api.exchange.gemini.baseurl}")
    private String baseUrl;
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
    public void fetchPairs() {
        try {
            List<SymbolResDTO> symbolDetails = new ArrayList<>();
            ResponseEntity<String> res = apiCaller(baseUrl+"/v1/symbols","GET");
            List<String> symbols= new ObjectMapper().readValue(res.getBody(), new TypeReference<List<String>>() {});
            List<String> filter = Arrays.asList("btcusd",
                    "ethbtc",
                    "batusd",
                    "linkusd",
                    "btceur",
                    "btcgbp");
            List<String> filterSymbols = symbols.stream().filter(symbol->filter.contains(symbol)).collect(Collectors.toList());

            //price feed
            ResponseEntity<String> resPriceFeed = apiCaller(baseUrl+"/v1/pricefeed","GET");
            List<PriceFeedRes> priceFeedRes = new ObjectMapper().readValue(resPriceFeed.getBody(), new TypeReference<List<PriceFeedRes>>() {});
            List<PriceFeedRes> filterPriceFeedRes = priceFeedRes.stream().filter(priceFeed->filter.contains(priceFeed.getPair().toLowerCase())).collect(Collectors.toList());

            Map<String,SymbolResDTO> symbolMap = new HashMap<String,SymbolResDTO>();
            for(String symbol : filterSymbols){
                ResponseEntity<String> symRes = apiCaller(baseUrl+"/v1/symbols/details/"+symbol,"GET");
                SymbolResponse response = new ObjectMapper().readValue(symRes.getBody(),SymbolResponse.class);

                Optional<PriceFeedRes> priceFeed = filterPriceFeedRes.stream().filter(p->p.getPair().toLowerCase().equals(response.getSymbol().toLowerCase())).findFirst();
                Double open24h = 0.0;
                if(priceFeed!=null &priceFeed.isPresent()){
                    open24h = priceFeed.get().getPrice() / (1 + (priceFeed.get().getPercentChange24h()/100));
                }
                SymbolResDTO symbolResDTO = GeminiUtil.getSymbolResDTO(response,open24h);
                symbolMap.put(response.getSymbol().toLowerCase(),symbolResDTO);
                symbolDetails.add(symbolResDTO);
            }
            GeminiUtil.setSymbolMap(symbolMap);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public List<SymbolResDTO> getPairs() {
        return new ArrayList<>(GeminiUtil.getPairs().values());
    }

    @Override
    public ResponseEntity<String> accountInfo(String apiKey, String secretKey, String passphrase) {
           Map<String,String> payload = new HashMap<>();
           payload.put("request","/v1/account");
           payload.put("account","primary");
           payload.put("nonce",String.valueOf(GeminiUtil.getNonce()));
        try {
            String json = new ObjectMapper().writeValueAsString(payload);
            byte[] b64 = GeminiUtil.getB64(json);
            String signature = GeminiUtil.getSignature(b64,secretKey);
            ResponseEntity<String> res = apiCaller(baseUrl+"/v1/account","POST",b64,signature,apiKey);
            return res;
        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
        public OrderResDTO createOrder(String apiKey, String secretKey, String passphrase, PlaceOrderReqDTO body) {
        CreateOrderRequest request = GeminiUtil.getCreateOrderReqDTO(body);
        request.setClientOrderId("12344545");
        request.setNonce(GeminiUtil.getNonce());
        request.setAccountType("primary");
        request.setRequest("/v1/order/new");

        try {
            String json = new ObjectMapper().writeValueAsString(request);
            byte[] b64 = GeminiUtil.getB64(json);
            String signature = GeminiUtil.getSignature(b64,secretKey);
            ResponseEntity<String> res = apiCaller(baseUrl+"/v1/order/new","POST",b64,signature,apiKey);
            // TODO exception handling if order fails
            CreateOrderResponse orderResponse = new ObjectMapper().readValue(res.getBody(), CreateOrderResponse.class);
            OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(orderResponse);
            try {
                orderResDTO.setSymbol(GeminiUtil.getStandardSymbol(orderResDTO.getSymbol()));
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            return orderResDTO;
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
        payload.put("nonce",String.valueOf(GeminiUtil.getNonce()));
        payload.put("account","primary");
        payload.put("request",url);
        try {
            String json = new ObjectMapper().writeValueAsString(payload);
            byte b64[] = Base64.getEncoder().encode(json.getBytes(StandardCharsets.UTF_8));
            String signature = GeminiUtil.getSignature(b64,secretKey);
            ResponseEntity<String> res  = apiCaller(baseUrl+url,"POST",b64,signature,apiKey);
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

    @Override
    public List<BalanceDTO> getBalance(String apiKey, String secretKey, String passPhrase) {
        Map<String,String> payload = new HashMap<>();
        payload.put("request","/v1/balances");
        payload.put("account","primary");
        payload.put("nonce",String.valueOf(GeminiUtil.getNonce()));
        try {
            String json = new ObjectMapper().writeValueAsString(payload);
            byte[] b64 = GeminiUtil.getB64(json);
            String signature = GeminiUtil.getSignature(b64,secretKey);
            ResponseEntity<String> res = apiCaller(baseUrl+"/v1/balances","POST",b64,signature,apiKey);
            List<BalanceGeminiDTO> balanceGeminiDTOs= new ObjectMapper().readValue(res.getBody(), new TypeReference<List<BalanceGeminiDTO>>() {});
            List<BalanceDTO> balanceDTOS = GeminiUtil.getStandardBalanceDTOs(balanceGeminiDTOs);
            return balanceDTOS;
        } catch (NoSuchAlgorithmException | InvalidKeyException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
