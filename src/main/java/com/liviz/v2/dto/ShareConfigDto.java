package com.liviz.v2.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ShareConfigDto {
    @Indexed(unique = true)
    @Size(max = 50)
    @NotEmpty
    private String name;

    @NotNull
    private String linkedProjectId;

    private String description;

    @NotNull
    private Boolean passwordProtected;

    @Size(max = 60)
    private String password;

}
