package com.liviz.v2.Project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProjectEditingDto {
    private String name;

    private Boolean isPublic;

    private String description;
}
