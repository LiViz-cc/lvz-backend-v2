package com.liviz.v2.serviceImpl;

import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.exception.UnauthenticatedException;
import com.liviz.v2.model.User;
import com.liviz.v2.service.AuthService;
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
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    RandomStringGenerator randomStringGenerator;

    @Override
    public Optional<User> signUp(AuthSignUpDto authSignUpDto) {
        // check if email already exists
        Optional<User> userOptional = userDao.findByEmail(authSignUpDto.getEmail());
        if (userOptional.isPresent()) {
            return Optional.empty();
        }

        // check if username already exists
        if (authSignUpDto.getUsername() == null) {
            authSignUpDto.setUsername(generateUsername(authSignUpDto.getEmail()));
        } else {
            userOptional = userDao.findByUsername(authSignUpDto.getUsername());
            if (userOptional.isPresent()) {
                return Optional.empty();
            }
        }

        // create user
        User user = new User(authSignUpDto.getEmail(), passwordEncoder.encode(authSignUpDto.getPassword()), authSignUpDto.getUsername());
        return Optional.of(userDao.save(user));
    }

    // refer to https://stackoverflow.com/questions/17947026/java-method-which-can-provide-the-same-output-as-python-method-for-hmac-sha256-i
    private String generateUsername(String email) {
        try {
            // get HMAC sha256 algorithm
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            // set secret key
            final String randomSecret = "wje9ewEWFX";
            SecretKeySpec secretKey = new SecretKeySpec(randomSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            // get HMAC sha256 hash
            byte[] hash = sha256_HMAC.doFinal(email.getBytes());
            // convert to hex string
            String check = Hex.encodeHexString(hash);
            // set username with first 10 characters of hex string
            return check.substring(0, 10);
        } catch (NoSuchAlgorithmException e) {
            logger.debug("NoSuchAlgorithmException, " + e.getMessage());
        } catch (InvalidKeyException e) {
            logger.debug("InvalidKeyException, " + e.getMessage());
        }
        return null;
    }

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


    @Override
    public Optional<User> createAnonymousUser() {
        // generate random username and password
        String username = randomStringGenerator.generateUsername(10);
        String password = randomStringGenerator.generatePassword(20);

        // pack authSignUpDto
        AuthSignUpDto authSignUpDto = new AuthSignUpDto();
        authSignUpDto.setUsername(username);
        authSignUpDto.setPassword(password);
        authSignUpDto.setEmail(username + "@anonymous.com");

        logger.info("username: " + username);
        logger.info("password: " + password);

        // sign up user
        return signUp(authSignUpDto);
    }
}
