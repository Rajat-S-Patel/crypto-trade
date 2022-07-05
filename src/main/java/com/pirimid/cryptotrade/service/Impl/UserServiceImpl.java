package com.pirimid.cryptotrade.service.Impl;

import com.pirimid.cryptotrade.model.User;
import com.pirimid.cryptotrade.repository.UserRepository;
import com.pirimid.cryptotrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Override
    public User getDefaultUser() {
        Optional<User> defaultUser = userRepository.findByEmail("default@mail.com");
        if(!defaultUser.isPresent()){
            User user = new User();
            user.setCreateDate(Date.from(Instant.now()));
            user.setEmail("default@mail.com");
            user.setPassword("##@@");
            user=userRepository.save(user);
            return user;
        }
        return defaultUser.get();
    }

    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
