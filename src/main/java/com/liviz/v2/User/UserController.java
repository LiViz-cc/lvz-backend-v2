package com.liviz.v2.User;

import com.liviz.v2.Auth.AuthResponseDto;
import com.liviz.v2.Auth.AuthSignUpDto;
import com.liviz.v2.Auth.ChangePasswordDto;
import com.liviz.v2.Auth.ChangeUsernameDto;
import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.exception.UnauthenticatedException;
import com.liviz.v2.Auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;
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
            throw new NoSuchElementFoundException("User not found");
        }

        // return unauthorized if jwt username is not equal to user id
        if (!userData.get().getId().equals(jwtUser.getId())) {
            throw new UnauthenticatedException("Unauthorized");
        }

        // return user
        return new ResponseEntity<>(userData.get(), HttpStatus.OK);
    }

    @GetMapping("/self")
    public ResponseEntity<User> getUserByJwtToken(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // get user by id
        Optional<User> userData = userService.findById(jwtUser.getId());

        // return not found if user is not found
        if (userData.isEmpty()) {
            throw new NoSuchElementFoundException("User not found");
        }

        // return user
        return new ResponseEntity<>(userData.get(), HttpStatus.OK);
    }

    @PostMapping("/{id}/password")
    public ResponseEntity<AuthResponseDto> changePassword(
            @PathVariable("id") String userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangePasswordDto changePasswordDto
    ) throws Exception {

        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // check password
        try {
            authService.authenticate(jwtUser.getUsername(), changePasswordDto.getOldPassword());
        } catch (UnauthenticatedException err) {
            throw new BadRequestException("Old password is wrong");
        }

        // change password
        AuthResponseDto userData = userService.changePassword(jwtUser, userId, changePasswordDto);

        // return ok if change password success
        return new ResponseEntity<>(userData, HttpStatus.OK);

    }

    @PostMapping("/{id}/add_password")
    public ResponseEntity<AuthResponseDto> addPassword(
            @PathVariable("id") String userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody AddPasswordDto addPasswordDto
    ) throws Exception {

        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // change password
        AuthResponseDto userData = userService.addPassword(jwtUser, userId, addPasswordDto);

        // return ok if change password success
        return new ResponseEntity<>(userData, HttpStatus.OK);

    }

    @PostMapping("/{id}/username")
    public ResponseEntity<AuthResponseDto> changeUsername(
            @PathVariable("id") String userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody ChangeUsernameDto changeUsernameDto
    ) throws Exception {

        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        // check password
        authService.authenticate(jwtUser.getUsername(), changeUsernameDto.getPassword());

        // change username
        AuthResponseDto userData = userService.changeUsername(jwtUser, userId, changeUsernameDto);

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

    @PostMapping("/{id}/authenticate")
    public ResponseEntity<AuthResponseDto> authenticateAnonymousUser(
            @PathVariable("id") String userId,
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody AuthSignUpDto authSignUpDto
    ) throws Exception {

        // get jwt user
        User jwtUser = jwtTokenUtil.getJwtUserFromToken(authorizationHeader);

        AuthResponseDto userData = userService.authenticateAnonymousUser(jwtUser, authSignUpDto);

        // return user
        return new ResponseEntity<>(userData, HttpStatus.OK);

    }
}
