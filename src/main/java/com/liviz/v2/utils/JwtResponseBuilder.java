package com.liviz.v2.utils;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.AuthResponseDto;
import com.liviz.v2.dto.JwtResponse;
import com.liviz.v2.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

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
