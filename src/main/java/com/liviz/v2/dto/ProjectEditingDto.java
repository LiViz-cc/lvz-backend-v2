package com.liviz.v2.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEditingDto {
    private String name;

    private Boolean isPublic;

    private String description;
}
