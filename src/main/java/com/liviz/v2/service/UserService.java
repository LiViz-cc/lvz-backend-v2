package com.liviz.v2.service;

import com.liviz.v2.dto.ChangePasswordDto;
import com.liviz.v2.dto.ChangeUsernameDto;
import com.liviz.v2.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    User changePassword(User jwtUser, String userId, ChangePasswordDto changePasswordDto);

    User changeUsername(User jwtUser, String userId, ChangeUsernameDto changeUsernameDto);

    User resetUser(User jwtUser, String userId);

    User createAnonymousUser();
}
