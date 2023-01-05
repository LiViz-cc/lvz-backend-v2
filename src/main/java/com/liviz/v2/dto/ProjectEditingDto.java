package com.liviz.v2.dto;

import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectEditingDto {
    private String name;

    private Boolean isPublic;

    private String description;
}
