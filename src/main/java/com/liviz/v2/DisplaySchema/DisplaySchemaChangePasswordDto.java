package com.liviz.v2.DisplaySchema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DisplaySchemaChangePasswordDto {

    private String oldPassword;
    private String newPassword;
}
