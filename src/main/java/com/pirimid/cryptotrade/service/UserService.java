package com.pirimid.cryptotrade.service;

import com.pirimid.cryptotrade.model.User;

import java.util.UUID;

public interface UserService {
    User getDefaultUser();
    User getUserById(UUID userId);
}
