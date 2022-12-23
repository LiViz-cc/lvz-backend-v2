package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liviz.v2.model.DataSourceSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DataSourcePostingDto {
    @NotEmpty
    private String name;

    @JsonProperty("public")
    private boolean isPublic;

    private String description;

    @JsonProperty("data_type")
    private String staticData;

    @JsonProperty("data_type")
    private String dataType;

    private String url;

    @NotNull
    private List<DataSourceSlot> slots;


}
