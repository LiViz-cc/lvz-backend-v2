package com.liviz.v2.Auth;

import com.liviz.v2.User.User;
import com.liviz.v2.User.UserDao;
import com.liviz.v2.config.JwtTokenUtil;
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
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setGoogleId(googleId);

            userDao.save(user);

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
