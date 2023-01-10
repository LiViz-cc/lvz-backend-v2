package com.liviz.v2.service;

import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.model.User;

import java.util.Optional;

public interface AuthService {
    Optional<User> signUp(AuthSignUpDto authSignUpDto);

    void authenticate(String username, String password) throws Exception;
}
