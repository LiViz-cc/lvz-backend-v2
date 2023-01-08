package com.liviz.v2.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ChangeUsernameDto {

    @NotBlank
    private String username;
    @NotBlank
    private String password;

}
