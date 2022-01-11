package com.pirimid.cryptotrade.util;

import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.coinbase.ExcCoinbase;
import com.pirimid.cryptotrade.helper.exchange.ExcParent;
import com.pirimid.cryptotrade.helper.exchange.gemini.ExcGemini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeUtil {
    @Autowired
    public ExcCoinbase exc_coinbase;
    @Autowired
    public ExcGemini excGemini;

    public ExcParent getObject(EXCHANGE exchange) {
        switch (exchange) {
            case COINBASE:
                return exc_coinbase;
            case GEMINI:
                return excGemini;

        }
        return null;
    }
}
