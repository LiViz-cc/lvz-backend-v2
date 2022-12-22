package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("data_source")
public class DataSource {
    @Id
    private String id;

    private String name;
    private boolean isPublic;
    private String description;
    private String static_data;
    private String data_type;
    private String url;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date created;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modified;

    @DBRef(lazy = true)
    private User created_by;


    public DataSource(String name, boolean isPublic, String description, String static_data, String data_type, String url) {
        this.name = name;
        this.isPublic = isPublic;
        this.description = description;
        this.static_data = static_data;
        this.data_type = data_type;
        this.url = url;
        this.created = new Date(System.currentTimeMillis());
        this.modified = new Date(System.currentTimeMillis());
    }

    public User getUser() {
        return created_by;
    }

    public User setUser(User user) {
        return created_by = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatic_data(String static_data) {
        this.static_data = static_data;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getCreated() {
        return created;
    }

    public long getModified() {
        return modified.getTime();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getDescription() {
        return description;
    }

    public String getStatic_data() {
        return static_data;
    }

    public String getData_type() {
        return data_type;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "DataSource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", isPublic=" + isPublic +
                ", description='" + description + '\'' +
                ", created_by=" + created_by +
                ", static_data='" + static_data + '\'' +
                ", data_type='" + data_type + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}