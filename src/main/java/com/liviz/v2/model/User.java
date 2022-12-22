package com.liviz.v2.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collation = "user")
public class User {
    @Id
    private String id;

    private String email;
    private Date created;
    private Date modified;
    private String username;

    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Date getCreate() {
        return created;
    }

    public Date getModified() {
        return modified;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", username='" + username + '\'' +
                '}';
    }
}
