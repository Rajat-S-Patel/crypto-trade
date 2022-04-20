package com.pirimid.cryptotrade.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.request.CreateOrderRequest;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CreateOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.SymbolResponse;
import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import com.pirimid.cryptotrade.websocket.privateWS.gemini.response.OrderResponse;
import com.pirimid.cryptotrade.websocket.privateWS.gemini.response.RestypeGemini;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class GeminiUtil {
    private static Map<String,SymbolResDTO> symbolMap = null;

    public static long getNonce(){
        return new Date().getTime()*10000;
    }
    
    public static byte[] getB64(String payload){
        return Base64.getEncoder().encode(payload.getBytes(StandardCharsets.UTF_8));
    }
    public static void setSymbolMap(Map<String,SymbolResDTO> map){
        symbolMap = map;
    }
    public static Map<String,SymbolResDTO> getPairs() {
        return symbolMap;
    }
    public static String getStandardSymbol(String excSymbol) throws RuntimeException{
        if(symbolMap == null) throw new RuntimeException("Uninitialized Symbol Map");
        return symbolMap.get(excSymbol).getSymbol();
    }
    public static String getSignature(byte[] payload,String secretKeyString) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        byte [] encodedKey = secretKeyString.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(encodedKey,"HmacSHA384");
        Mac hmacSHA384 = Mac.getInstance("HmacSHA384");
        hmacSHA384.init(secretKey);

        return HexUtils.toHexString(hmacSHA384.doFinal(payload));
    }

    public static OrderResDTO getPlaceOrderResDTO(CreateOrderResponse response){
        if(response.getOrderId() == null) {
           return null;
        }
        String type = response.getType();
        OrderResDTO orderResDTO = OrderResDTO.builder()
                .exchangeOrderId(response.getOrderId() == null?"":response.getOrderId().toString())
                .price(response.getPrice())
                .size(response.getOriginalAmount())
                .symbol(response.getSymbol())
                .side(Side.valueOf(response.getSide().toUpperCase()))
                .type(OrderType.valueOf(type.substring(type.indexOf(" ")+1).toUpperCase()))
                .createdAt(new Date(response.getTimestamp()))
                .executedAmount(response.getExecutedAmount())
                .build();

        if(response.isLive())
            orderResDTO.setStatus(Status.valueOf("PENDING"));
        else orderResDTO.setStatus(Status.valueOf("FILLED"));
        return orderResDTO;
    }
    public static String getExchangeSymbol(String symbol){
        int splitPosition = symbol.indexOf("-");
        return symbol.substring(0,splitPosition)+symbol.substring(splitPosition+1);
    }
    public static CreateOrderRequest getCreateOrderReqDTO(PlaceOrderReqDTO req){
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .symbol(getExchangeSymbol(req.getSymbol()))
                .amount(req.getSize())
                .price(req.getPrice())
                .side(req.getSide().getValue())
                .type("exchange "+req.getType().getValue())
                .build();
        return createOrderRequest;
    }

    public static SymbolResDTO getSymbolResDTO(SymbolResponse response) {
        SymbolResDTO dto = SymbolResDTO.builder()
                .symbol(response.getBaseCurrency()+"-"+response.getQuoteCurrency())
                .base(response.getBaseCurrency())
                .quote(response.getQuoteCurrency())
                .minOrderSize(response.getMinOrderSize())
                .build();
        return dto;
    }

    public static OrderResDTO getPlaceOrderResDTO(OrderResponse response){
        String type=response.getOrderType();
        OrderResDTO order = OrderResDTO.builder()
                .exchangeOrderId(response.getOrderId())
                .price(response.getPrice())
                .size(response.getOriginalAmount())
                .symbol(response.getSymbol())
                .side(Side.valueOf(response.getSide().toUpperCase()))
                .type(OrderType.valueOf(type.substring(type.indexOf(" ")+1).toUpperCase()))
                .executedAmount(response.getExecutedAmount())
                .build();

        if(RestypeGemini.valueOf(response.getType().toUpperCase()) == RestypeGemini.ACCEPTED){
            order.setStatus(Status.NEW);
            order.setCreatedAt(response.getTimestampms());
        }
        else if(RestypeGemini.valueOf(response.getType().toUpperCase()) == RestypeGemini.FILL && response.getExecutedAmount() > 0){
            order.setStatus(Status.PARTIALLY_FILLED);
        }
        else if(response.isCancelled()){
            order.setStatus(Status.CANCELLED);
            order.setEndAt(response.getTimestampms());
        }
        else if(RestypeGemini.valueOf(response.getType().toUpperCase()) == RestypeGemini.CLOSED){
            order.setStatus(Status.FILLED);
            order.setEndAt(response.getTimestampms());
        }
        else if(RestypeGemini.valueOf(response.getType().toUpperCase()) == RestypeGemini.REJECTED){
            order.setStatus(Status.REJECTED);
            order.setEndAt(response.getTimestampms());
        }
        return order;
    }

    public static TradeDto getTradeDTO(OrderResponse response){
        TradeDto trade = TradeDto.builder()
                .tradeId(response.getFill().getTradeId())
                .exchangeOrderId(response.getOrderId())
                .fee(response.getFill().getFee())
                .side(Side.valueOf(response.getSide().toUpperCase()))
                .size(response.getOriginalAmount())
                .price(response.getPrice())
                .time(response.getTimestampms())
                .symbol(response.getSymbol())
                .build();
        return trade;
    }
}
