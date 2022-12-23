package com.liviz.v2.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class DataSourceSlot {
    @NotEmpty
    private String name;

    @NotEmpty
    private String slot_type;

    @Field("optional")
    @NotNull
    private boolean isOptional;

    @Field("default")
    private String defaultValue;

    @Field("alias")
    private String aliasValue;

    public DataSourceSlot(DataSourceSlot dataSourceSlot) {
        this.name = dataSourceSlot.name;
        this.slot_type = dataSourceSlot.slot_type;
        this.isOptional = dataSourceSlot.isOptional;
        this.defaultValue = dataSourceSlot.defaultValue;
        this.aliasValue = dataSourceSlot.aliasValue;
    }


}
