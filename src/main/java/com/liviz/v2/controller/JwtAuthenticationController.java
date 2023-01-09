package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.dto.JwtResponse;
import com.liviz.v2.dto.JwtRequest;
import com.liviz.v2.model.User;
import com.liviz.v2.service.AuthService;
import com.liviz.v2.service.JWTUserDetailsService;
import com.liviz.v2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        Optional<User> userOptional = authService.signUp(authSignUpDto);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        return ResponseEntity.ok(userOptional.get());
    }
}