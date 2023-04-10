package com.liviz.v2.Auth;

import com.liviz.v2.User.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponseDto {
    private JwtResponse token;
    private User user;
}
