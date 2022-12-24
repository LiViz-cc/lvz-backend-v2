package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Date;

@Document("user")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    @Email
    private String email;

    @JsonIgnore
    @Size(min = 60, max = 60)
    private String password;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modifiedTime;

    @Indexed(unique = true)
    @NonNull
    private String username;

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.createdTime = new Date(System.currentTimeMillis());
        this.modifiedTime = new Date(System.currentTimeMillis());
    }

}
