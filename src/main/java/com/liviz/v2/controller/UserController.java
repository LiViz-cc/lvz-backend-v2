package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.model.User;
import com.liviz.v2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable("id") String id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // get jwt username
        String username = jwtTokenUtil.getJwtIdentity(authorizationHeader);

        // return unauthorized if jwt username is null
        if (username == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // get user by id
        Optional<User> userData = userService.findById(id);

        // return not found if user is not found
        if (userData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // return unauthorized if jwt username is not equal to user id
        if (!userData.get().getUsername().equals(username)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // return user
        return new ResponseEntity<>(userData.get(), HttpStatus.OK);
    }


}
