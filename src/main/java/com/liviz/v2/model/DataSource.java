package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Document("data_source")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataSource {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    @Field("public")
    private boolean isPublic;
    private String description;
    private String static_data;
    @Field("data_type")
    private String dataType;
    private String url;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date created;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modified;

    @DBRef(lazy = true)
    @Field("created_by")
    @JsonProperty("created_by")
    private User createdBy;

    @DBRef(lazy = true)
    private List<Project> projects;

    private DataSourceExample dataSourceExample;

    @NotNull
    @Field("slots")
    private List<DataSourceSlot> dataSourceSlots;

    public DataSource(String name, boolean isPublic, String description, String static_data, String data_type, String url, List<DataSourceSlot> dataSourceSlots) {
        this.name = name;
        this.isPublic = isPublic;
        this.description = description;
        this.static_data = static_data;
        this.dataType = data_type;
        this.url = url;

        // deep copy dataSourceSlots
        if (dataSourceSlots != null) {
            this.dataSourceSlots = dataSourceSlots.stream().map(DataSourceSlot::new).collect(Collectors.toList());
        }

        // set timestamp
        this.created = new Date(System.currentTimeMillis());
        this.modified = new Date(System.currentTimeMillis());
    }

    public DataSource(DataSource other) {
        this.id = other.id;
        this.name = other.name;
        this.isPublic = other.isPublic;
        this.description = other.description;
        this.static_data = other.static_data;
        this.dataType = other.dataType;
        this.url = other.url;
        this.created = other.created;
        this.modified = other.modified;
        this.createdBy = other.createdBy;
        this.projects = other.projects;

        // deep copy of dataSourceExample and dataSourceSlots
        this.dataSourceExample = new DataSourceExample(other.dataSourceExample);
        this.dataSourceSlots = other.dataSourceSlots.stream().map(DataSourceSlot::new).collect(Collectors.toList());
    }


}
