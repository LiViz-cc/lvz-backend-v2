package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@AllArgsConstructor
@NoArgsConstructor
public class DisplaySchemaDto {
    @Indexed(unique = true)
    @Length(max = 50)
    @JsonProperty("name")
    private String name;

    @JsonProperty("public")
    @NotNull
    private Boolean isPublic = false;

    @JsonProperty("description")
    @Size(max = 1000)
    private String description = "";

    @JsonProperty("echarts_option")
    // Json string
    private String eChartOption;

    @DBRef
    @JsonProperty("linked_project")
    private Project linkedProject;
}
