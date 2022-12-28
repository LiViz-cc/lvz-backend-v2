package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.liviz.v2.model.Project;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DisplaySchemaDto {
    @Indexed(unique = true)
    @Length(max = 50)
    private String name;

    @NotNull
    private Boolean isPublic = false;

    @Size(max = 1000)
    private String description = "";

    // Json string
    private String eChartOption;

    @DBRef
    private String linkedProjectId;
}
