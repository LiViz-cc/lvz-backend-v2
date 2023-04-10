package com.liviz.v2.Project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectDto {
    @NotBlank
    private String name;

    @NotNull
    private Boolean isPublic;

    @NotNull
    private String description;
}
