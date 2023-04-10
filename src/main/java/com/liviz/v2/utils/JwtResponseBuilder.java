package com.liviz.v2.utils;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.Auth.AuthResponseDto;
import com.liviz.v2.Auth.JwtResponse;
import com.liviz.v2.User.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class JwtResponseBuilder {
    public AuthResponseDto build(User user, JwtTokenUtil jwtTokenUtil) {
        // get userDetails
        final UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());

        // generate token
        final String token = jwtTokenUtil.generateToken(userDetails);

        // return token and user
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setUser(user);
        authResponseDto.setToken(new JwtResponse(token));
        return authResponseDto;
    }
}
