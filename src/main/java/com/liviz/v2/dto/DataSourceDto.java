package com.liviz.v2.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.liviz.v2.model.DataSourceSlot;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataSourceDto {
    // TODO: enable all validations
    @NotBlank
    private String name;

    private Boolean isPublic;

    private String description;

    private String staticData;

    @NotBlank
    private String dataType;

    @URL
    @NotBlank
    private String url = "";

    @JsonSetter(nulls = Nulls.SKIP)
    private List<DataSourceSlot> slots = new ArrayList<>();


}
