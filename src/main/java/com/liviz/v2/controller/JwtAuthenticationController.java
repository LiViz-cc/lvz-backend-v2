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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@CrossOrigin
@RequestMapping("/auth")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JWTUserDetailsService userDetailsService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    // TODO: support login by email
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest jwtRequest) throws Exception {
        // Important: check password!
        authenticate(jwtRequest.getUsername(), jwtRequest.getPassword());

        // get user details
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(jwtRequest.getUsername());

        // generate token
        final String token = jwtTokenUtil.generateToken(userDetails);

        // get user
        final Optional<User> userOptional = userService.findByUsername(jwtRequest.getUsername());

        // return not found if user not found
        if (userOptional.isEmpty()){
            return ResponseEntity.status(401).body("User not found");
        }

        // return token and user
        Map<String, Object> response = new HashMap<>();
        response.put("token", new JwtResponse(token));
        response.put("user", userOptional.get());
        return ResponseEntity.ok(response);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody AuthSignUpDto authSignUpDto) {
        Optional<User> userOptional = authService.signUp(authSignUpDto);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        return ResponseEntity.ok(userOptional.get());
    }
}