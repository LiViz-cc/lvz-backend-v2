package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document("project")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Project {
    @Id
    private String id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modifiedTime;

    @DBRef(lazy = true)
    private User createdBy;

    private boolean isPublic;

    private String description;

    @DBRef(lazy = true)
    private List<DataSource> dataSources;

    public Project(String name, Date createdTime, Date modifiedTime, User createdBy, boolean isPublic, String description) {
        this.name = name;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.createdBy = createdBy;
        this.isPublic = isPublic;
        this.description = description;
    }

    public Project(Project other) {
        this.id = other.id;
        this.name = other.name;
        this.createdTime = other.createdTime;
        this.modifiedTime = other.modifiedTime;
        this.createdBy = other.createdBy;
        this.isPublic = other.isPublic;
        this.description = other.description;
        this.dataSources = other.dataSources;
    }

}
