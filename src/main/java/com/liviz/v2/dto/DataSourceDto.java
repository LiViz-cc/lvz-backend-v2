package com.liviz.v2.dto;

import com.liviz.v2.model.DataSourceSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DataSourceDto {
    @NotBlank
    private String name;

    private boolean isPublic;

    private String description;

    private String staticData;

    private String dataType;

    private String url;

    @NotNull
    private List<DataSourceSlot> slots;


}
