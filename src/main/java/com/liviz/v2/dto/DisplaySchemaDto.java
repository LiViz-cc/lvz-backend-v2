package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DisplaySchemaDto {
    @Indexed(unique = true)
    @Length(max = 50)
    @NotBlank
    private String name;

    @NotNull
    private Boolean isPublic;

    @Size(max = 1000)
    @JsonSetter(nulls = Nulls.SKIP)
    private String description = "";

    // Json string
    private String eChartOption;

    @DBRef
    private String linkedProjectId;
}
