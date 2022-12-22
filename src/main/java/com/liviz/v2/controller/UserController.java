package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.UserDao;
import com.liviz.v2.model.User;
import com.liviz.v2.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable("id") String id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Unauthorized");
        }
        String bearerToken = authorizationHeader.substring("Bearer ".length());

        // use the bearer token to authenticate the request
        Optional<User> userData = userService.findById(id);

        if (userData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        if (!jwtTokenUtil.validateToken(bearerToken, userData.get().getUsername())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(userData.get(), HttpStatus.OK);
    }


}
