package com.pirimid.cryptotrade.util;

import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.coinbase.EXC_Coinbase;
import com.pirimid.cryptotrade.helper.exchange.EXC_Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeUtil {
    @Autowired
    public EXC_Coinbase exc_coinbase;

    public EXC_Parent getObject(EXCHANGE exchange){
        switch (exchange){
            case COINBASE:
                return exc_coinbase;
            case KUCOIN:
                break;
        }
        return null;
    }
}
