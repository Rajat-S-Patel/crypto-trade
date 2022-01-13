package com.pirimid.cryptotrade.util;

import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.ExcParent;
import com.pirimid.cryptotrade.helper.exchange.gemini.ExcGemini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeUtil {

    @Autowired
    public ExcGemini gemini;

    public ExcParent getObject(EXCHANGE exchange){
        switch (exchange){
            case GEMINI:
                return gemini;
            default:
                break;
        }
        return null;
    }
}
