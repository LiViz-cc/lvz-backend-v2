package com.liviz.v2.Auth;

import com.liviz.v2.Auth.AuthSignUpDto;
import com.liviz.v2.User.User;

import java.util.Optional;

public interface AuthService {

    void authenticate(String username, String password) throws Exception;

}
