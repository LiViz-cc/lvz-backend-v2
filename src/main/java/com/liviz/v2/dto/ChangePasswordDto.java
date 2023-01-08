package com.liviz.v2.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ChangePasswordDto {

    @NotBlank
    private String oldPassword;
    // raw password
    @NotBlank
    private String newPassword;

}
