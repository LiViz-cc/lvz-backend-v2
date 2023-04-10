package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.AuthResponseDto;
import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.dto.JwtRequest;
import com.liviz.v2.dto.JwtResponse;
import com.liviz.v2.model.User;
import com.liviz.v2.service.AuthService;
import com.liviz.v2.service.JWTUserDetailsService;
import com.liviz.v2.service.UserService;
import com.liviz.v2.serviceImpl.AuthServiceImpl;
import com.liviz.v2.serviceImpl.JWTUserDetailsServiceImpl;
import com.liviz.v2.serviceImpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class JwtAuthenticationController {
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    JWTUserDetailsService userDetailsService;

    @Autowired
    AuthService authService;

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest jwtRequest) throws Exception {
        // get user by username or email
        Optional<User> userOptional;
        if (jwtRequest.getUsername() != null) {
            userOptional = userService.findByUsername(jwtRequest.getUsername());
        } else if (jwtRequest.getEmail() != null) {
            userOptional = userService.findByEmail(jwtRequest.getEmail());
        } else {
            return ResponseEntity.badRequest().body("Username and email are null");
        }

        // return bad request if user is not found
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        final User user = userOptional.get();

        // Important: check password!
        authService.authenticate(user.getUsername(), jwtRequest.getPassword());

        // get user details
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getUsername());

        // generate token
        final String token = jwtTokenUtil.generateToken(userDetails);

        // return token and user
        Map<String, Object> response = new HashMap<>();
        response.put("token", new JwtResponse(token));
        response.put("user", userOptional.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody AuthSignUpDto authSignUpDto) {
        // sign up
        Optional<User> userOptional = authService.signUp(authSignUpDto);

        // return bad request if user is not found
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        // get user details
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(userOptional.get().getUsername());

        // generate token
        final String token = jwtTokenUtil.generateToken(userDetails);

        // return token and user
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setUser(userOptional.get());
        authResponseDto.setToken(new JwtResponse(token));
        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/create_anonymous")
    public ResponseEntity<?> createAnonymousUser() {
        Optional<User> userOptional = authService.createAnonymousUser();
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Cannot create anonymous user");
        }

        // get user details
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(userOptional.get().getUsername());

        // generate token
        final String token = jwtTokenUtil.generateToken(userDetails);

        // return token and user
        Map<String, Object> response = new HashMap<>();
        response.put("token", new JwtResponse(token));
        response.put("user", userOptional.get());
        return ResponseEntity.ok(response);
    }
}
