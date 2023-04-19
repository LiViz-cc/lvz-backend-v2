package com.liviz.v2.Project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.liviz.v2.DataSource.DataSource;
import com.liviz.v2.DisplaySchema.DisplaySchema;
import com.liviz.v2.ShareConfig.ShareConfig;
import com.liviz.v2.User.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document("project")
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class Project implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modifiedTime;

    @DBRef(lazy = true)
    private User createdBy;

    private Boolean isPublic;

    private String description;

    @DBRef(lazy = true)
    private List<DataSource> dataSources = new ArrayList<>();

    @DBRef(lazy = true)
    private DisplaySchema displaySchema;

    @DBRef(lazy = true)
    private List<ShareConfig> shareConfigs = new ArrayList<>();

    public Project(String name, Date createdTime, Date modifiedTime, User createdBy, Boolean isPublic, String description) {
        this.name = name;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.createdBy = createdBy;
        this.isPublic = isPublic;
        this.description = description;
    }

    // shallow copy
    public Project(Project other) {
        this.id = other.id;
        this.name = other.name;
        this.createdTime = other.createdTime;
        this.modifiedTime = other.modifiedTime;
        this.createdBy = other.createdBy;
        this.isPublic = other.isPublic;
        this.description = other.description;
        this.dataSources = other.dataSources;
        this.displaySchema = other.displaySchema;
        this.shareConfigs = other.shareConfigs;
    }

    public void addDataSource(DataSource dataSource) {
        this.dataSources.add(dataSource);
    }

    public void addShareConfig(ShareConfig shareConfig) {
        this.shareConfigs.add(shareConfig);
    }

    public void removeShareConfig(ShareConfig shareConfig) {
        this.shareConfigs.remove(shareConfig);
    }


}
