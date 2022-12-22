package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Document("project")
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setCreated_by(User created_by) {
        this.created_by = created_by;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public User getCreated_by() {
        return created_by;
    }

    public String getDescription() {
        return description;
    }
}
