package com.pirimid.cryptotrade.helper;

public enum EXCHANGE {
    AAX("AAX"),
    COINBASE("Coinbase"),
    GEMINI("Gemini"),
    KUCOIN("Kucoin");
    String value;
    EXCHANGE(String value){
        this.value = value;
    }
}
