package com.liviz.v2.User;

import com.liviz.v2.Auth.AuthResponseDto;
import com.liviz.v2.Auth.AuthSignUpDto;
import com.liviz.v2.Auth.ChangePasswordDto;
import com.liviz.v2.Auth.ChangeUsernameDto;
import com.liviz.v2.User.User;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    AuthResponseDto changePassword(User jwtUser, String userId, ChangePasswordDto changePasswordDto);

    AuthResponseDto changeUsername(User jwtUser, String userId, ChangeUsernameDto changeUsernameDto);

    User resetUser(User jwtUser, String userId);

    AuthResponseDto authenticateAnonymousUser(User jwtUser, AuthSignUpDto authSignUpDto);

    AuthResponseDto addPassword(@NotNull User jwtUser, @NotNull String userId, @NotNull AddPasswordDto addPasswordDto);

    Optional<User> findUserByJwtToken(String jwtToken);
}
