package com.liviz.v2.dto;

public class AuthDto {
    private String email;

    // raw password
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
