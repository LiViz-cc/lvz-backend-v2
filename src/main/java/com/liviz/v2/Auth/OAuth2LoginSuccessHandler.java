package com.liviz.v2.Auth;

import com.liviz.v2.exception.UnauthenticatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    OauthService oauthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // get user info
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        // check if user is from google
        if ("google".equals(token.getAuthorizedClientRegistrationId())) {
            AuthResponseDto authResponseDto = oauthService.googleOauthSuccess(token);
            response.sendRedirect("http://localhost:8081/loginSuccess?token=" + authResponseDto.getToken().getJwtToken());
        } else {
            throw new UnauthenticatedException("Unsupported Oauth2 provider");
        }

    }
}
