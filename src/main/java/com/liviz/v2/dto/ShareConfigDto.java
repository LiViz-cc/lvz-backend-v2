package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShareConfigDto {
    @Indexed(unique = true)
    @Size(max = 50)
    @NotEmpty
    private String name;

    @NotNull
    private String linkedProjectId;

    private String description;

    @NotNull
    private Boolean passwordProtected;

    @Size(max = 60)
    private String password;

}
