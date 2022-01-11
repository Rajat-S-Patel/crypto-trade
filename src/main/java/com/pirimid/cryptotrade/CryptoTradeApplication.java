package com.pirimid.cryptotrade;

import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.time.Instant;

@SpringBootApplication
public class CryptoTradeApplication {
    @Autowired
    private UserRepository userRepository;
    private User user=null;
    @Bean
    public User getUser(){
        if(user==null){
            user = new User();
            user.setCreateDate(Date.from(Instant.now()));
            user.setEmail("default@mail.com");
            user.setPassword("##@@");
            user=userRepository.save(user);
        }
        return user;
    }
    public static void main(String[] args) {
        SpringApplication.run(CryptoTradeApplication.class, args);
    }

}
