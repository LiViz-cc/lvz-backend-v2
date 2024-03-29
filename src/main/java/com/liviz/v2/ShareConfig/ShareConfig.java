package com.liviz.v2.ShareConfig;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.liviz.v2.Project.Project;
import com.liviz.v2.User.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Document("share_config")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class ShareConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Indexed(unique = true)
    @Size(max = 50)
    @NotEmpty
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modifiedTime;

    @DBRef
    @NotNull
    private User createdBy;

    @DBRef
    @NotNull
    private Project linkedProject;

    private String description;

    @NotNull
    private Boolean passwordProtected;

    @JsonIgnore
    @Size(max = 60)
    private String password;

}
