package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthSignUpDto {
    private String email;

    // raw password
    @JsonIgnore
    private String password;
    private String username;

}
