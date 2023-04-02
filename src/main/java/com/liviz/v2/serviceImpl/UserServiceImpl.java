package com.liviz.v2.serviceImpl;

import com.liviz.v2.dao.*;
import com.liviz.v2.dto.AuthSignUpDto;
import com.liviz.v2.dto.ChangePasswordDto;
import com.liviz.v2.dto.ChangeUsernameDto;
import com.liviz.v2.exception.UnauthenticatedException;
import com.liviz.v2.model.User;
import com.liviz.v2.service.AuthService;
import com.liviz.v2.service.UserService;
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

}
