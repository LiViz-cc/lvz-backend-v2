package com.liviz.v2.Auth;

import com.liviz.v2.User.UserDao;
import com.liviz.v2.Auth.JWTUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JWTUserDetailsServiceImpl implements JWTUserDetailsService {
    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.liviz.v2.User.User> user = userDao.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new User(user.get().getUsername(), user.get().getPassword(),
                new ArrayList<>());
    }

}
