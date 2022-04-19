package com.pirimid.cryptotrade.util;

import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.PlaceOrderReqDTO;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.request.PlaceOrderReqCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.PlaceOrderResCoinbaseDTO;
import com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response.SymbolResCoinbaseDTO;
import com.pirimid.cryptotrade.model.OrderType;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.model.Status;
import com.pirimid.cryptotrade.websocket.privateWS.coinbase.res.Restype;
import com.pirimid.cryptotrade.websocket.privateWS.coinbase.res.WSCoinbaseOrderDto;
import com.pirimid.cryptotrade.websocket.privateWS.coinbase.res.WSCoinbaseTradeDto;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
public class CoinbaseUtil {
    private static Map<String, SymbolResDTO> symbolMap = null;

    public static void setSymbolMap(Map<String,SymbolResDTO> map){
        symbolMap = map;
    }
    public static Map<String,SymbolResDTO> getPairs() {
        return symbolMap;
    }
    public static String getStandardSymbol(String excSymbol) throws RuntimeException{
        if(symbolMap==null) throw new RuntimeException("Uninitialized Symbol Map");
        return symbolMap.get(excSymbol).getSymbol();
    }

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
        OrderResDTO orderResDTO = OrderResDTO.builder()
                .exchangeOrderId(placeOrderResCoinbaseDTO.getId().toString())
                .price(placeOrderResCoinbaseDTO.getPrice())
                .size(placeOrderResCoinbaseDTO.getSize())
                .symbol(placeOrderResCoinbaseDTO.getProductId())
                .side(Side.valueOf(placeOrderResCoinbaseDTO.getSide().toUpperCase()))
                .type(OrderType.valueOf(placeOrderResCoinbaseDTO.getType().toUpperCase()))
                .createdAt(placeOrderResCoinbaseDTO.getCreatedAt())
                .executedAmount(placeOrderResCoinbaseDTO.getExecutedValue())
                .status(Status.valueOf(placeOrderResCoinbaseDTO.getStatus().toUpperCase()))
                .funds(placeOrderResCoinbaseDTO.getFunds())
                .build();
        return orderResDTO;
    }

    public static OrderResDTO getWsPlaceOrderResDTO(WSCoinbaseOrderDto wsCoinbaseOrderDto) {
        OrderResDTO orderResDTO = OrderResDTO.builder()
                .exchangeOrderId(wsCoinbaseOrderDto.getOrderId())
                .price(wsCoinbaseOrderDto.getPrice())
                .funds(wsCoinbaseOrderDto.getFunds())
                .size(wsCoinbaseOrderDto.getSize())
                .symbol(wsCoinbaseOrderDto.getProductId())
                .side(Side.valueOf(wsCoinbaseOrderDto.getSide().toUpperCase()))
                .exchangeUserId(wsCoinbaseOrderDto.getProfileId().toString())
                .build();
        String type = wsCoinbaseOrderDto.getType();
        if (wsCoinbaseOrderDto.getOrderType() != null) {
            orderResDTO.setType(OrderType.valueOf(wsCoinbaseOrderDto.getOrderType().toUpperCase()));
        }

        if (Restype.RECEIVED == Restype.valueOf(type.toUpperCase())) {
            orderResDTO.setCreatedAt(wsCoinbaseOrderDto.getTime());
            orderResDTO.setStatus(Status.valueOf("OPEN"));
        } else if (Restype.DONE == Restype.valueOf(type.toUpperCase())) {
            orderResDTO.setEndAt(wsCoinbaseOrderDto.getTime());
            orderResDTO.setStatus(Status.valueOf("FILLED"));
        }
        return orderResDTO;
    }

    public static TradeDto getWsTradeResDTO(WSCoinbaseTradeDto wsCoinbaseTradeDto) {

        TradeDto tradeDto = TradeDto.builder()
                .tradeId(wsCoinbaseTradeDto.getTradeId())
                .exchangeOrderId(wsCoinbaseTradeDto.getTakerOrderId())
                .price(wsCoinbaseTradeDto.getPrice())
                .size(wsCoinbaseTradeDto.getSize())
                .funds(wsCoinbaseTradeDto.getFunds())
                .symbol(wsCoinbaseTradeDto.getProductId())
                .side(Side.valueOf(wsCoinbaseTradeDto.getSide().toUpperCase()))
                .fee(wsCoinbaseTradeDto.getTakerFeeRate())
                .time(wsCoinbaseTradeDto.getTime())
                .build();
        if (wsCoinbaseTradeDto.getFunds() == null) {
            tradeDto.setFunds(wsCoinbaseTradeDto.getSize() * wsCoinbaseTradeDto.getPrice());
        }
        return tradeDto;
    }

    public static PlaceOrderReqCoinbaseDTO getPlaceOrderReqDTO(PlaceOrderReqDTO placeOrderReqDTO) {
        PlaceOrderReqCoinbaseDTO placeOrderReqCoinbaseDTO = PlaceOrderReqCoinbaseDTO.builder()
                .type(placeOrderReqDTO.getType().getValue())
                .productId(placeOrderReqDTO.getSymbol())
                .side(placeOrderReqDTO.getSide().getValue())
                .funds(placeOrderReqDTO.getFunds())
                .size(placeOrderReqDTO.getSize())
                .price(placeOrderReqDTO.getPrice())
                .build();
        return placeOrderReqCoinbaseDTO;
    }

    public static List<SymbolResDTO> getPairsResDTO(List<SymbolResCoinbaseDTO> symbolResCoinbaseDTOS) {
        List<SymbolResDTO> symbolResDTOS = new ArrayList<>();
        for (SymbolResCoinbaseDTO symbol : symbolResCoinbaseDTOS) {
            if (symbol != null) {
                SymbolResDTO symbolResDTO = SymbolResDTO.builder()
                        .symbol(symbol.getBaseCurrency()+"-"+symbol.getQuoteCurrency())
                        .base(symbol.getBaseCurrency())
                        .quote(symbol.getQuoteCurrency())
                        .minOrderSize(symbol.getBaseMinSize())
                        .minMarketFunds(symbol.getMinMarketFunds())
                        .build();
                symbolResDTOS.add(symbolResDTO);
            }
        }
        return symbolResDTOS;

    }


}
