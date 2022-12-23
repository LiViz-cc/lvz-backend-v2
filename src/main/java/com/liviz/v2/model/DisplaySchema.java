package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Document("display_schema")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DisplaySchema {
    @Indexed(unique = true)
    @Length(max = 50)
    @Field("name")
    private String name;

    @Field("created")
    @NotEmpty
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date createdTime;

    @Field("modified")
    @NotEmpty
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modifiedTime;

    @Field("created_by")
    @DBRef
    private User createdBy;

    @Field("public")
    @NotNull
    private Boolean isPublic;

    @Field("description")
    @Size(max = 1000)
    private String description;

    @Field("echarts_option")
    // Json string
    private String eChartOption;

    @DBRef
    @Field("linked_project")
    private Project linkedProject;

}
