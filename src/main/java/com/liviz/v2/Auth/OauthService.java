package com.liviz.v2.Auth;

import com.liviz.v2.User.User;
import com.liviz.v2.User.UserDao;
import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OauthService {
    @Autowired
    UserDao userDao;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    RandomStringGenerator randomStringGenerator;

    @Autowired
    SignUpService signUpService;

    public AuthResponseDto googleOauthSuccess(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        OAuth2User oAuth2user = oAuth2AuthenticationToken.getPrincipal();
        Map<String, Object> userAttributes = oAuth2user.getAttributes();


        System.out.println(userAttributes);

        String email = (String) userAttributes.get("email");
        String name = (String) userAttributes.get("name");
        String googleId = (String) userAttributes.get("sub");

        // find user by google id
        Optional<User> userOptional = userDao.findByGoogleId(googleId);

        User user;

        /* if user not found, create new user and then provide jwt token */
        if (userOptional.isEmpty()) {
            AuthSignUpDto authSignUpDto = new AuthSignUpDto();
            authSignUpDto.setEmail(email);
            authSignUpDto.setUsername(name);
            authSignUpDto.setPassword(randomStringGenerator.generatePassword(20));
            authSignUpDto.setHasPassword(false);
            authSignUpDto.setGoogleId(googleId);
            Optional<User> userOptionalSignUp = signUpService.signUp(authSignUpDto);

            if (userOptionalSignUp.isEmpty()) {
                throw new RuntimeException("Cannot create user");
            }
            user = userOptionalSignUp.get();

        } else {
            user = userOptional.get();
        }

        // generate token
        final String token = jwtTokenUtil.generateToken(user.getUsername());


        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setUser(user);
        authResponseDto.setToken(new JwtResponse(token));
        return authResponseDto;

    }
}
