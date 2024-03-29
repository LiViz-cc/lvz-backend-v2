package com.liviz.v2.DataSource;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class DataSourceSlot {
    @NotBlank
    private String name;

    @NotBlank
    private String slotType;

    @NotNull
    private Boolean isOptional;

    private String defaultValue;

    private String aliasValue;


    public DataSourceSlot(DataSourceSlot dataSourceSlot) {
        this.name = dataSourceSlot.name;
        this.slotType = dataSourceSlot.slotType;
        this.isOptional = dataSourceSlot.isOptional;
        this.defaultValue = dataSourceSlot.defaultValue;
        this.aliasValue = dataSourceSlot.aliasValue;
    }
}
