package com.liviz.v2.serviceImpl;

import com.liviz.v2.config.JwtTokenUtil;
import com.liviz.v2.dao.*;
import com.liviz.v2.dto.AuthResponseDto;
import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.dto.ChangePasswordDto;
import com.liviz.v2.dto.ChangeUsernameDto;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.UnauthenticatedException;
import com.liviz.v2.model.User;
import com.liviz.v2.service.AuthService;
import com.liviz.v2.service.UserService;
import com.liviz.v2.utils.JwtResponseBuilder;
import com.liviz.v2.utils.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    DataSourceDao dataSourceDao;

    @Autowired
    ProjectDao projectDao;

    @Autowired
    ShareConfigDao shareConfigDao;

    @Autowired
    DisplaySchemaDao displaySchemaDao;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Autowired
    JwtResponseBuilder jwtResponseBuilder;

    @Override
    public Optional<User> findById(String id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }


    @Override
    public AuthResponseDto changePassword(User jwtUser, String userId, ChangePasswordDto changePasswordDto) {
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
        userDao.save(user);

        // save user
        return jwtResponseBuilder.build(user, jwtTokenUtil);

    }

    @Override
    public User changeUsername(User jwtUser, String userId, ChangeUsernameDto changeUsernameDto) {
        // get user by id
        User user = userDao.findById(userId).orElseThrow(() -> new UnauthenticatedException("Unauthorized"));

        // return unauthorized if jwt username is not equal to user id
        if (!userId.equals(jwtUser.getId())) {
            throw new UnauthenticatedException("Unauthorized");
        }

        // change username
        user.setUsername(changeUsernameDto.getUsername());

        // save user
        return userDao.save(user);
    }

    @Override
    public User resetUser(User jwtUser, String userId) {
        // **NOTE: DANGEROUS OPERATION!!!**

        // get user by id
        User user = userDao.findById(userId).orElseThrow(() -> new UnauthenticatedException("Unauthorized"));

        // return unauthorized if jwt username is not equal to user id
        if (!userId.equals(jwtUser.getId())) {
            throw new UnauthenticatedException("Unauthorized");
        }

        // delete all models linked to user
        dataSourceDao.deleteAllByUserId(userId);
        projectDao.deleteAllByUserId(userId);
        shareConfigDao.deleteAllByUserId(userId);
        displaySchemaDao.deleteAllByUserId(userId);

        // save user
        return userDao.save(user);
    }

    @Override
    public User authenticateAnonymousUser(User jwtUser, AuthSignUpDto authSignUpDto) {
        // if user is not anonymous
        if (!jwtUser.getEmail().endsWith("@anonymous.com")) {
            throw new BadRequestException("User is not anonymous. No need to authenticate");
        }

        // email most be provided
        if (authSignUpDto.getEmail() == null || authSignUpDto.getEmail().isEmpty()) {
            throw new BadRequestException("Email is required");
        }

        // check if email is already taken
        if (userDao.findByEmail(authSignUpDto.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already taken");
        }

        // check if username is already taken
        if (authSignUpDto.getUsername() != null
                && !authSignUpDto.getUsername().isEmpty()
                && userDao.findByUsername(authSignUpDto.getUsername()).isPresent()) {
            throw new BadRequestException("Username is already taken");
        }

        // change username and password
        jwtUser.setPassword(passwordEncoder.encode(authSignUpDto.getPassword()));
        jwtUser.setEmail(authSignUpDto.getEmail());

        // if username is not provided, keep the old one
        if (authSignUpDto.getUsername() != null && !authSignUpDto.getUsername().isEmpty()) {
            jwtUser.setUsername(authSignUpDto.getUsername());
        }

        // save user
        return userDao.save(jwtUser);
    }

}
