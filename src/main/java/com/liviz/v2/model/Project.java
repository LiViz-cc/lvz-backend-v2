package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private Date created;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modified;

    @DBRef(lazy = true)
    private User created_by;

    @Field("public")
    private boolean isPublic;

    private String description;

    @DBRef(lazy = true)
    private List<DataSource> dataSources;

    public Project(String name, User created_by, boolean isPublic, String description) {
        this.name = name;
        this.created_by = created_by;
        this.isPublic = isPublic;
        this.description = description;
    }

    public Project(Project other) {
        this.id = other.id;
        this.name = other.name;
        this.created = other.created;
        this.modified = other.modified;
        this.created_by = other.created_by;
        this.isPublic = other.isPublic;
        this.description = other.description;
        this.dataSources = other.dataSources;
    }

}
