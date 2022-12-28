package com.liviz.v2.dto;

import com.liviz.v2.model.DataSourceSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DataSourceDto {
    // TODO: validation not working
    @NotBlank
    private String name;

    // TODO: default value not working
    private Boolean isPublic = false;

    private String description;

    private String staticData;

    private String dataType;

    @URL
    @NotBlank
    private String url;

    @NotNull
    private List<DataSourceSlot> slots;


}
