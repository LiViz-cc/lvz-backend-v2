package com.liviz.v2.Auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class AuthSignUpDto {
    private String email;

    // raw password
    @NotBlank
    private String password;
    private String username;

    private Boolean hasPassword = true;

    private String googleId;

}
