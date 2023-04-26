package com.liviz.v2.Auth;

import com.liviz.v2.User.User;
import com.liviz.v2.User.UserDao;
import com.liviz.v2.utils.RandomStringGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class SignUpService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    RandomStringGenerator randomStringGenerator;

    public Optional<User> signUp(AuthSignUpDto authSignUpDto) {
        // check if email already exists
        Optional<User> userOptional = userDao.findByEmail(authSignUpDto.getEmail());
        if (userOptional.isPresent()) {
            return Optional.empty();
        }

        // check if username already exists
        if (authSignUpDto.getUsername() == null) {
            authSignUpDto.setUsername(randomStringGenerator.generateUsername(authSignUpDto.getEmail()));
        } else {
            userOptional = userDao.findByUsername(authSignUpDto.getUsername());
            if (userOptional.isPresent()) {
                return Optional.empty();
            }
        }

        // create user
        User user = new User(authSignUpDto.getEmail(), passwordEncoder.encode(authSignUpDto.getPassword()), authSignUpDto.getUsername());

        // check if it has password
        if (authSignUpDto.getHasPassword() == null) {
            user.setHasPassword(true);
        } else {
            user.setHasPassword(authSignUpDto.getHasPassword());
        }

        // check if it has Google id
        if (authSignUpDto.getGoogleId() != null) {
            user.setGoogleId(authSignUpDto.getGoogleId());
        }

        return Optional.of(userDao.save(user));
    }

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
