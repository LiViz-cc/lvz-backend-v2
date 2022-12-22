package com.liviz.v2.service;

import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.AuthDto;
import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> signUp(AuthSignUpDto authSignUpDto) {
        Optional<User> userOptional = userDao.findByEmail(authSignUpDto.getEmail());
        if (userOptional.isPresent()) {
            return Optional.empty();
        }

        User user = new User(authSignUpDto.getEmail(), passwordEncoder.encode(authSignUpDto.getPassword()), authSignUpDto.getUsername());
        return Optional.of(userDao.save(user));
    }
}
