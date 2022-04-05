package com.pirimid.cryptotrade.publicwebsocket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface publicWS {
    void connect() throws ExecutionException, InterruptedException, TimeoutException;
}
