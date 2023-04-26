package com.liviz.v2.Auth;

import com.liviz.v2.User.UserDao;
import com.liviz.v2.Auth.AuthSignUpDto;
import com.liviz.v2.exception.UnauthenticatedException;
import com.liviz.v2.User.User;
import com.liviz.v2.Auth.AuthService;
import com.liviz.v2.utils.RandomStringGenerator;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Component
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new UnauthenticatedException("USER_DISABLED");
        } catch (BadCredentialsException e) {
            throw new UnauthenticatedException("INVALID_CREDENTIALS");
        }
    }


}
