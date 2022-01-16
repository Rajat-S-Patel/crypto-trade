package com.pirimid.cryptotrade.helper.exchange;

public enum EXCHANGE {
    COINBASE("Coinbase"),
    GEMINI("Gemini");
    String value;

    EXCHANGE(String value){
        this.value = value;
    }
}
