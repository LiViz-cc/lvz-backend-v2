package com.liviz.v2.service;

import com.liviz.v2.dao.UserDao;
import com.liviz.v2.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public Optional<User> findById(String id) {
        return userDao.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }


}
