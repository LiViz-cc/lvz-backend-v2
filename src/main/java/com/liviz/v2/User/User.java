package com.liviz.v2.User;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Document("user")
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = {"id"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
    @NotBlank
    private String username;

    @JsonIgnore
    private String googleId;

    @JsonIgnore
    private Boolean hasPassword = true;

    public User(){
        this.createdTime = new Date(System.currentTimeMillis());
        this.modifiedTime = new Date(System.currentTimeMillis());
    }

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.createdTime = new Date(System.currentTimeMillis());
        this.modifiedTime = new Date(System.currentTimeMillis());
    }

}
