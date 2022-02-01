package com.pirimid.cryptotrade;

import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;

@SpringBootApplication
public class CryptoTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoTradeApplication.class, args);
    }

}
