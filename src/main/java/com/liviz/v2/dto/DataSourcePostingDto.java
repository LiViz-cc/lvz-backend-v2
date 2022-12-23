package com.liviz.v2.dto;

import com.liviz.v2.model.DataSourceSlot;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DataSourcePostingDto {
    @NotEmpty
    private String name;

    private boolean isPublic;

    private String description;

    private String static_data;

    private String data_type;

    private String url;

    @NotNull
    private DataSourceSlot dataSourceSlot;

    public DataSourcePostingDto(String name, boolean isPublic, String description, String static_data, String data_type, String url) {
        this.name = name;
        this.isPublic = isPublic;
        this.description = description;
        this.static_data = static_data;
        this.data_type = data_type;
        this.url = url;
    }

}
