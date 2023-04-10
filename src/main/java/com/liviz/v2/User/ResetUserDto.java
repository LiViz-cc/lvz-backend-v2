package com.liviz.v2.User;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class ResetUserDto {
    @NotBlank
    private String password;

}
