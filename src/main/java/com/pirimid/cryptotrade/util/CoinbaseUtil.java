package com.pirimid.cryptotrade.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class CoinbaseUtil {

   /* @Value("${api.exchange.coinbase.secretkey}")
    static String secretKeyString;
*/
   private static String secretKeyString="QSbSkFaeoy7yb/yIwPSoQDom8LbYQR23ESAu3CXpnchZy5AKKJEb15bBuZ0jUvUk5Up/pEY5L8cect7arDz70A==";;

   public static String getSignature(String timestamp,String method,String path,String body) throws NoSuchAlgorithmException, InvalidKeyException {
        System.out.println(secretKeyString);
        String prehash = timestamp+method+path+body;
        byte[] secretKeyDecoded = Base64.getDecoder().decode(secretKeyString);
        SecretKey secretKey = new SecretKeySpec(secretKeyDecoded, "HmacSHA256");
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        hmacSha256.init(secretKey);
        System.out.println("hello");
        return Base64.getEncoder().encodeToString(hmacSha256.doFinal(prehash.getBytes()));
    }
}
