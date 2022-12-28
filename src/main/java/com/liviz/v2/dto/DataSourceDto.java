package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.liviz.v2.model.DataSourceSlot;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
// TODO: use `NoArgsConstructor` in all DTOs
@NoArgsConstructor
public class DataSourceDto {
    // TODO: enable all validations
    @NotBlank
    private String name;

    // TODO: default value not working
    @JsonSetter(nulls = Nulls.SKIP)
    private Boolean isPublic;

    private String description;

    private String staticData;

    private String dataType;

    @URL
    @NotBlank
    private String url = "";

    @NotNull
    private List<DataSourceSlot> slots;


}
