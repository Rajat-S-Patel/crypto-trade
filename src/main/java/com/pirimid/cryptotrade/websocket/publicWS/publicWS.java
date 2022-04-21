package com.pirimid.cryptotrade.websocket.publicWS;

import com.pirimid.cryptotrade.DTO.SymbolResDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface publicWS {
    void connect() throws ExecutionException, InterruptedException, TimeoutException;
}
