package com.pirimid.cryptotrade.util;

import com.pirimid.cryptotrade.helper.EXCHANGE;
import com.pirimid.cryptotrade.helper.EXC_AAX;
import com.pirimid.cryptotrade.helper.EXC_Coinbase;
import com.pirimid.cryptotrade.helper.EXC_Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeUtil {
    @Autowired
    public EXC_AAX exc_aax;
    @Autowired
    public EXC_Coinbase exc_coinbase;

    public EXC_Parent getObject(EXCHANGE exchange){
        switch (exchange){
            case AAX:
                return exc_aax;
            case COINBASE:
                return exc_coinbase;
            case KUCOIN:
                break;
        }
        return null;
    }
}
