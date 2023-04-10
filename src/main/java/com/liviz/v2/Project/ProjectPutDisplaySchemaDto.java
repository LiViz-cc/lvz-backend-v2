package com.liviz.v2.Project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProjectPutDisplaySchemaDto {
    @NotNull
    private String displaySchemaId;
}
