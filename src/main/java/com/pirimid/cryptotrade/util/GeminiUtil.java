package com.pirimid.cryptotrade.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.request.CreateOrderRequest;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.CreateOrderResponse;
import com.pirimid.cryptotrade.helper.exchange.gemini.dto.response.SymbolResponse;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import com.pirimid.cryptotrade.model.Trade;
import com.pirimid.cryptotrade.websocket.gemini.response.OrderResponse;
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
    public static long getNonce(){
        return new Date().getTime();
    }
    public static byte[] getB64(String payload){
        return Base64.getEncoder().encode(payload.getBytes(StandardCharsets.UTF_8));
    }
    public static String getSignature(byte[] payload,String secretKeyString) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        byte [] encodedKey = secretKeyString.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(encodedKey,"HmacSHA384");
        Mac hmacSHA384 = Mac.getInstance("HmacSHA384");
        hmacSHA384.init(secretKey);

        return HexUtils.toHexString(hmacSHA384.doFinal(payload));
    }

    public static OrderResDTO getPlaceOrderResDTO(CreateOrderResponse response){
        OrderResDTO orderResDTO = new OrderResDTO();
        orderResDTO.setExchangeOrderId(response.getOrderId().toString());
        orderResDTO.setPrice(response.getPrice());
        orderResDTO.setSize(response.getOriginalAmount());
        orderResDTO.setSymbol(response.getSymbol());
        orderResDTO.setSide(Side.valueOf(response.getSide().toUpperCase()));
        String type = response.getType();
        orderResDTO.setType(OrderType.valueOf(type.substring(type.indexOf(" ")+1).toUpperCase()));
        orderResDTO.setCreatedAt(new Date(response.getTimestamp()));
        orderResDTO.setExecutedAmount(response.getExecutedAmount());
        if(response.isLive())
            orderResDTO.setStatus(Status.valueOf("PENDING"));
        else orderResDTO.setStatus(Status.valueOf("FILLED"));
        return orderResDTO;
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

    public static OrderResDTO getPlaceOrderResDTO(OrderResponse response){
        OrderResDTO order = new OrderResDTO();
        order.setExchangeOrderId(response.getOrderId());
        order.setPrice(response.getPrice());
        order.setSize(response.getOriginalAmount());
        order.setSymbol(response.getSymbol());
        order.setSide(Side.valueOf(response.getSide().toUpperCase()));
        String type=response.getOrderType();
        order.setType(OrderType.valueOf(type.substring(type.indexOf(" ")+1).toUpperCase()));
        order.setCreatedAt(response.getTimestampms());
        order.setExecutedAmount(response.getExecutedAmount());

        if(response.getType().equals("accepted")){
            order.setStatus(Status.NEW);
        }
        else if(response.getType().equals("fill") && response.getExecutedAmount() > 0){
            order.setStatus(Status.PARTIALLY_FILLED);
        }
        else if(response.isCancelled()){
            order.setStatus(Status.CANCELLED);
        }
        else if(response.getType().equals("closed")){
            order.setStatus(Status.FILLED);
        }
        else if(response.getType().equals("rejected")){
            order.setStatus(Status.REJECTED);
        }
        return order;
    }

    public static TradeDto getTradeDTO(OrderResponse response){
        TradeDto trade = new TradeDto();
        trade.setTradeId(response.getFill().getTradeId());
        trade.setExchangeOrderId(response.getOrderId());
        trade.setFee(response.getFill().getFee());
        trade.setSide(Side.valueOf(response.getSide().toUpperCase()));
        trade.setSize(response.getOriginalAmount());
        trade.setPrice(response.getPrice());
        trade.setTime(response.getTimestampms());
        trade.setSymbol(response.getSymbol());
        return trade;
    }
}
