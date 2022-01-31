package com.pirimid.cryptotrade.util;

import com.google.gson.Gson;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.request.PlaceOrderReqCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.PlaceOrderResCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.SymbolResCoinbaseDTO;
import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import com.pirimid.cryptotrade.websocket.coinbase.res.WSCoinbaseOrderDto;
import com.pirimid.cryptotrade.websocket.coinbase.res.WsCoinbaseTradeDto;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class CoinbaseUtil {


    public static String getSignature(String timestamp, String secretKeyString, String method, String path, String body) throws NoSuchAlgorithmException, InvalidKeyException {
        String prehash = timestamp + method + path + body;
        byte[] secretKeyDecoded = Base64.getDecoder().decode(secretKeyString);
        SecretKey secretKey = new SecretKeySpec(secretKeyDecoded, "HmacSHA256");
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(secretKey);
        String sin = Base64.getEncoder().encodeToString(hmacSha256.doFinal(prehash.getBytes()));

        return sin;
    }

    public static OrderResDTO getPlaceOrderResDTO(PlaceOrderResCoinbaseDTO placeOrderResCoinbaseDTO) {
        OrderResDTO orderResDTO;
        Gson gson = new Gson();
        String orderCoinbaseres = gson.toJson(placeOrderResCoinbaseDTO);
        orderResDTO = gson.fromJson(orderCoinbaseres, OrderResDTO.class);
        orderResDTO.setExecutedAmount(placeOrderResCoinbaseDTO.getExecuted_value());
        orderResDTO.setType(OrderType.valueOf(placeOrderResCoinbaseDTO.getType().toUpperCase()));
        orderResDTO.setSide(Side.valueOf(placeOrderResCoinbaseDTO.getSide().toUpperCase()));
        orderResDTO.setStatus(Status.valueOf(placeOrderResCoinbaseDTO.getStatus().toUpperCase()));
        orderResDTO.setCreatedAt(placeOrderResCoinbaseDTO.getCreated_at());
        orderResDTO.setSymbol(placeOrderResCoinbaseDTO.getProduct_id());
        orderResDTO.setFunds(placeOrderResCoinbaseDTO.getFunds());
        orderResDTO.setExchangeOrderId(placeOrderResCoinbaseDTO.getId().toString());
        return orderResDTO;
    }

    public static OrderResDTO getWsPlaceOrderResDTO(WSCoinbaseOrderDto wsCoinbaseOrderDto) {
        OrderResDTO orderResDTO = new OrderResDTO();
        String type = wsCoinbaseOrderDto.getType();
        orderResDTO.setExchangeOrderId(wsCoinbaseOrderDto.getOrder_id());
        orderResDTO.setPrice(wsCoinbaseOrderDto.getPrice());
        orderResDTO.setFunds(wsCoinbaseOrderDto.getFunds());
        orderResDTO.setSize(wsCoinbaseOrderDto.getSize());
        orderResDTO.setSymbol(wsCoinbaseOrderDto.getProduct_id());
        orderResDTO.setSide(Side.valueOf(wsCoinbaseOrderDto.getSide().toUpperCase()));
        if (wsCoinbaseOrderDto.getOrder_type() != null) {
            orderResDTO.setType(OrderType.valueOf(wsCoinbaseOrderDto.getOrder_type().toUpperCase()));
        }

        if (type.equals("received")) {
            orderResDTO.setCreatedAt(wsCoinbaseOrderDto.getTime());
            orderResDTO.setStatus(Status.valueOf("OPEN"));
        } else if (type.equals("done")) {
            orderResDTO.setEndAt(wsCoinbaseOrderDto.getTime());
            orderResDTO.setStatus(Status.valueOf("FILLED"));
        }
        orderResDTO.setExchangeUserId(wsCoinbaseOrderDto.getProfile_id().toString());
        return orderResDTO;
    }

    public static TradeDto getWsTradeResDTO(WsCoinbaseTradeDto wsCoinbaseTradeDto) {

        TradeDto tradeDto = new TradeDto();
        tradeDto.setTradeId(wsCoinbaseTradeDto.getTrade_id());
        tradeDto.setExchangeOrderId(wsCoinbaseTradeDto.getTaker_order_id());
        tradeDto.setPrice(wsCoinbaseTradeDto.getPrice());
        tradeDto.setSize(wsCoinbaseTradeDto.getSize());
        tradeDto.setFunds(wsCoinbaseTradeDto.getFunds());
        if (wsCoinbaseTradeDto.getFunds() == null) {
            tradeDto.setFunds(wsCoinbaseTradeDto.getSize() * wsCoinbaseTradeDto.getPrice());
        }

        tradeDto.setSymbol(wsCoinbaseTradeDto.getProduct_id());
        tradeDto.setSide(Side.valueOf(wsCoinbaseTradeDto.getSide().toUpperCase()));
        tradeDto.setFee(wsCoinbaseTradeDto.getTaker_fee_rate());
        tradeDto.setTime(wsCoinbaseTradeDto.getTime());
        return tradeDto;
    }

    public static PlaceOrderReqCoinbaseDTO getPlaceOrderReqDTO(PlaceOrderReqDTO placeOrderReqDTO) {
        PlaceOrderReqCoinbaseDTO placeOrderReqCoinbaseDTO;
        Gson gson = new Gson();
        String orderCoinbasereq = gson.toJson(placeOrderReqDTO);
        placeOrderReqCoinbaseDTO = gson.fromJson(orderCoinbasereq, PlaceOrderReqCoinbaseDTO.class);
        placeOrderReqCoinbaseDTO.setType(placeOrderReqDTO.getType().getValue());
        placeOrderReqCoinbaseDTO.setSide(placeOrderReqDTO.getSide().getValue());
        placeOrderReqCoinbaseDTO.setProduct_id(placeOrderReqDTO.getSymbol());
        return placeOrderReqCoinbaseDTO;
    }

    public static List<SymbolResDTO> getPairsResDTO(List<SymbolResCoinbaseDTO> symbolResCoinbaseDTOS) {
        List<SymbolResDTO> symbolResDTOS = new ArrayList<>();
        for (SymbolResCoinbaseDTO symbol : symbolResCoinbaseDTOS) {
            if (symbol != null) {
                SymbolResDTO symbolResDTO = new SymbolResDTO(symbol.getId(), symbol.getBase_currency(), symbol.getQuote_currency(), symbol.getBase_min_size(), symbol.getMin_market_funds());
                symbolResDTOS.add(symbolResDTO);
            }
        }
        return symbolResDTOS;

    }


}
