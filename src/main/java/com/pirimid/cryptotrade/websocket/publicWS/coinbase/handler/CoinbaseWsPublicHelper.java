package com.pirimid.cryptotrade.websocket.publicWS.coinbase.handler;

import com.pirimid.cryptotrade.websocket.publicWS.WsTickerDto;
import com.pirimid.cryptotrade.websocket.publicWS.coinbase.dto.TickerCoinbaseDto;

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
