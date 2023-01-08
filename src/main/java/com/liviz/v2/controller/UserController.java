package com.liviz.v2.controller;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dto.ChangePasswordDto;
import com.liviz.v2.dto.ChangeUsernameDto;
import com.liviz.v2.dto.ResetUserDto;
import com.liviz.v2.model.User;
import com.liviz.v2.service.AuthService;
import com.liviz.v2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthService authService;


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable("id") String id,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // get user by id
        Optional<User> userData = userService.findById(id);

        // return not found if user is not found
        if (userData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // return unauthorized if jwt username is not equal to user id
        if (!userData.get().getId().equals(jwtUser.getId())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // return user
        return new ResponseEntity<>(userData.get(), HttpStatus.OK);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<User> changePassword(
            @PathVariable("id") String userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangePasswordDto changePasswordDto
    ) throws Exception {

        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // check password
        authService.authenticate(jwtUser.getUsername(), changePasswordDto.getOldPassword());

        // change password
        User userData = userService.changePassword(jwtUser, userId, changePasswordDto);

        // return ok if change password success
        return new ResponseEntity<>(userData, HttpStatus.OK);

    }

    @PostMapping("/{id}/username")
    public ResponseEntity<User> changeUsername(
            @PathVariable("id") String userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangeUsernameDto changeUsernameDto
    ) throws Exception {

        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // check password
        authService.authenticate(jwtUser.getUsername(), changeUsernameDto.getPassword());

        // change username
        User userData = userService.changeUsername(jwtUser, userId, changeUsernameDto);

        // return user
        return new ResponseEntity<>(userData, HttpStatus.OK);

    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<User> resetUser(
            @PathVariable("id") String userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ResetUserDto resetUserDto
    ) throws Exception {

        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // check password
        authService.authenticate(jwtUser.getUsername(), resetUserDto.getPassword());

        // reset user
        User userData = userService.resetUser(jwtUser, userId);

        // return user
        return new ResponseEntity<>(userData, HttpStatus.OK);

    }
}
