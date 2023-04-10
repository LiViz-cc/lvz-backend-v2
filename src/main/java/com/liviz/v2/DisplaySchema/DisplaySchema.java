package com.liviz.v2.DisplaySchema;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.liviz.v2.Project.Project;
import com.liviz.v2.User.User;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Document("display_schema")
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class DisplaySchema {
    @Id
    private String id;

    @Indexed(unique = true)
    @Length(max = 50)
    private String name;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date createdTime;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modifiedTime;

    @DBRef
    private User createdBy;

    @NotNull
    private Boolean isPublic;

    @Size(max = 1000)
    private String description;

    // Json string
    private String eChartOption;

    @DBRef
    private Project linkedProject;


    public DisplaySchema(String name, Date createdTime, Date modifiedTime, User createdBy, Boolean isPublic, String description, String eChartOption, Project linkedProject) {
        this.name = name;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.createdBy = createdBy;
        this.isPublic = isPublic;
        this.description = description;
        this.eChartOption = eChartOption;
        this.linkedProject = linkedProject;
    }


}
