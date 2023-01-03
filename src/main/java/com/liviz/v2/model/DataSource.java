package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Document("data_source")
@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class DataSource {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private Boolean isPublic;
    private String description;
    private String staticData;
    private String dataType;

    @URL
    private String url;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date createdTime;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date modifiedTime;

    @DBRef(lazy = true)
    private User createdBy;

    @DBRef(lazy = true)
    private List<Project> projects = new ArrayList<>();

    private DataSourceExample dataSourceExample;

    @NotNull
    private List<DataSourceSlot> dataSourceSlots;

    public DataSource(String name, Boolean isPublic, String description, String static_data, String data_type, String url, List<DataSourceSlot> dataSourceSlots) {
        this.name = name;
        this.isPublic = isPublic;
        this.description = description;
        this.staticData = static_data;
        this.dataType = data_type;
        this.url = url;

        // deep copy dataSourceSlots
        if (dataSourceSlots != null) {
            this.dataSourceSlots = dataSourceSlots.stream().map(DataSourceSlot::new).collect(Collectors.toList());
        }

        // set timestamp
        this.createdTime = new Date(System.currentTimeMillis());
        this.modifiedTime = new Date(System.currentTimeMillis());
    }

    public DataSource(DataSource other) {
        this.id = other.id;
        this.name = other.name;
        this.isPublic = other.isPublic;
        this.description = other.description;
        this.staticData = other.staticData;
        this.dataType = other.dataType;
        this.url = other.url;
        this.createdTime = other.createdTime;
        this.modifiedTime = other.modifiedTime;
        this.createdBy = other.createdBy;
        this.projects = other.projects;

        // TODO: shallow copy is fine
        // deep copy of dataSourceExample and dataSourceSlots
        this.dataSourceExample = new DataSourceExample(other.dataSourceExample);
        this.dataSourceSlots = other.dataSourceSlots.stream().map(DataSourceSlot::new).collect(Collectors.toList());
    }


}
