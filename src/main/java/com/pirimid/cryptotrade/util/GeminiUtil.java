package com.pirimid.cryptotrade.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderResDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.request.CreateOrderRequest;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CreateOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.SymbolResponse;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class GeminiUtil {
//    private static final String secretKeyString="2vSMZS9pPukfknhi4wyejguV6rMV";

    /*
        API Name: My Gemini Master API Key #1
        API Key: master-sIFFhvWpYBof8QH2XECh
        API Secret: 2vSMZS9pPukfknhi4wyejguV6rMV
     */
    public static String getSignature(byte[] payload,String secretKeyString) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        byte [] encodedKey = secretKeyString.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(encodedKey,"HmacSHA384");
        Mac hmacSHA384 = Mac.getInstance("HmacSHA384");
        hmacSHA384.init(secretKey);

        return HexUtils.toHexString(hmacSHA384.doFinal(payload));
    }

    public static PlaceOrderResDTO getPlaceOrderResDTO(CreateOrderResponse response){
        PlaceOrderResDTO placeOrderResDTO = new PlaceOrderResDTO();
        placeOrderResDTO.setId(response.getOrderId().toString());
        placeOrderResDTO.setPrice(response.getPrice());
        placeOrderResDTO.setSize(response.getOriginalAmount());
        placeOrderResDTO.setSymbol(response.getSymbol());
        placeOrderResDTO.setSide(response.getSide());
        placeOrderResDTO.setType(response.getType());
        placeOrderResDTO.setCreatedAt(new Date(response.getTimestamp()));
        placeOrderResDTO.setExecuted_amount(response.getExecutedAmount());
        if(response.isLive())
            placeOrderResDTO.setStatus("pending");
        else placeOrderResDTO.setStatus("completed");
        return placeOrderResDTO;
    }

    public static CreateOrderRequest getCreateOrderReqDTO(PlaceOrderReqDTO req){
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setSymbol(req.getSymbol());
        createOrderRequest.setAmount(req.getSize());
        createOrderRequest.setPrice(req.getPrice());
        createOrderRequest.setSide(req.getSide());
        createOrderRequest.setType("exchange "+req.getType());
        return createOrderRequest;
    }

    public static SymbolResDTO getSymbolResDTO(SymbolResponse response) {
        SymbolResDTO dto = new SymbolResDTO();
        dto.setSymbol(response.getBaseCurrency()+"/"+response.getQuoteCurrency());
        dto.setBase(response.getBaseCurrency());
        dto.setQuote(response.getQuoteCurrency());
        dto.setMinOrderSize(response.getMinOrderSize());
        return dto;
    }
}
