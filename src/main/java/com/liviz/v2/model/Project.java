package com.liviz.v2.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.util.Date;

@Document(collation = "project")
public class Project {
    @Id
    private String id;

    private String name;
    private Date created;
    private Date modified;

    @DBRef(lazy = true)
    private User created_by;

    @Field("public")
    private boolean isPublic;

    private String description;

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
