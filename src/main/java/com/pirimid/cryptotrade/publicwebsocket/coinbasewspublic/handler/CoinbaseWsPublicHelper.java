package com.pirimid.cryptotrade.publicwebsocket.coinbasewspublic.handler;

import com.pirimid.cryptotrade.publicwebsocket.WsTickerDto;
import com.pirimid.cryptotrade.publicwebsocket.coinbasewspublic.dto.TickerCoinbaseDto;
import lombok.Builder;

public class CoinbaseWsPublicHelper {
    private WsTickerDto normaliseData(TickerCoinbaseDto tickerCoinbaseDto){
        WsTickerDto wsTickerDto = WsTickerDto.builder()
                .symbol(tickerCoinbaseDto.getProductId())
                .price(tickerCoinbaseDto.getPrice())
                .high(tickerCoinbaseDto.getHigh24h())
                .low(tickerCoinbaseDto.getLow24h())
                .volume(tickerCoinbaseDto.getVolume24h())
                .percentChange(((tickerCoinbaseDto.getPrice()/tickerCoinbaseDto.getOpen24h())-1)*100)
                .build();
        return wsTickerDto;
    }

    public void emitData(TickerCoinbaseDto tickerCoinbaseDto){
        WsTickerDto wsTickerDto = normaliseData(tickerCoinbaseDto);
        System.out.println(wsTickerDto.toString());
    }
}
