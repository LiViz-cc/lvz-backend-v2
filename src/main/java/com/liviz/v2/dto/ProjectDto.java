package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.Nulls;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectDto {
    @NotBlank
    private String name;

    // TODO: use box type in all models

    @NotNull
    private Boolean isPublic;

    @NotNull
    private String description;

}
