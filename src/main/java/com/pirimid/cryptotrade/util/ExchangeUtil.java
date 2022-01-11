package com.pirimid.cryptotrade.util;

import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.helper.exchange.EXC_Parent;
import com.pirimid.cryptotrade.helper.exchange.gemini.EXC_Gemini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExchangeUtil {

    @Autowired
    public EXC_Gemini gemini;

    public EXC_Parent getObject(EXCHANGE exchange){
        switch (exchange){
            case GEMINI:
                return gemini;
            default:
                break;
        }
        return null;
    }
}
