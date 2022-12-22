package com.liviz.v2.service;

import com.liviz.v2.dao.UserDao;
import com.liviz.v2.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @GetMapping("/users/{id}")
    public Optional<User> findById(@PathVariable("id") String id) {
        return userDao.findById(id);
    }
}