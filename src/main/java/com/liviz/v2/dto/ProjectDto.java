package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liviz.v2.model.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    @NotBlank
    private String name;

    @NotNull
    private boolean isPublic;

    private String description = "";
}
