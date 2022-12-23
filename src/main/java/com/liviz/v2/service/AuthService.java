package com.liviz.v2.service;

import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.model.User;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Log logger = LogFactory.getLog(this.getClass());

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

}
