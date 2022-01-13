package com.pirimid.cryptotrade.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderResDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.request.CreateOrderRequest;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CreateOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.SymbolResponse;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;
import com.pirimid.cryptotrade.model.OrderType;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class GeminiUtil {

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
        placeOrderResDTO.setSide(Side.valueOf(response.getSide().toUpperCase()));
        String type = response.getType();
        placeOrderResDTO.setType(OrderType.valueOf(type.substring(type.indexOf(" ")+1).toUpperCase()));
        placeOrderResDTO.setCreatedAt(new Date(response.getTimestamp()));
        placeOrderResDTO.setExecutedAmount(response.getExecutedAmount());
        if(response.isLive())
            placeOrderResDTO.setStatus(Status.valueOf("PENDING"));
        else placeOrderResDTO.setStatus(Status.valueOf("FILLED"));
        return placeOrderResDTO;
    }

    public static CreateOrderRequest getCreateOrderReqDTO(PlaceOrderReqDTO req){
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setSymbol(req.getSymbol());          // logic to parse it to standard symbol DTO is not yet implemented
        createOrderRequest.setAmount(req.getSize());
        createOrderRequest.setPrice(req.getPrice());
        createOrderRequest.setSide(req.getSide().getValue());
        createOrderRequest.setType("exchange "+req.getType().getValue());
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
