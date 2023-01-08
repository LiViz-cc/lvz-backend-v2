package com.liviz.v2.service;

import com.liviz.v2.dao.UserDao;
import com.liviz.v2.dto.ChangePasswordDto;
import com.liviz.v2.exception.UnauthenticatedException;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Optional<User> findById(String id) {
        return userDao.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }


    public User changePassword(User jwtUser, String userId, ChangePasswordDto changePasswordDto) {
        // return unauthorized if jwt username is not equal to user id
        if (!userId.equals(jwtUser.getId())) {
            throw new UnauthenticatedException("Unauthorized");
        }

        // newPassword should be different from old password
        if (changePasswordDto.getOldPassword().equals(changePasswordDto.getNewPassword())) {
            throw new UnauthenticatedException("New password should be different from old password");
        }

        // get user by id
        User user = userDao.findById(userId).orElseThrow(() -> new UnauthenticatedException("Unauthorized"));

        // encode password
        String encodedPassword = passwordEncoder.encode(changePasswordDto.getNewPassword());

        // update password
        user.setPassword(encodedPassword);

        // save user
        return userDao.save(user);

    }
}
