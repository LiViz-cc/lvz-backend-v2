package com.liviz.v2.model;

import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
}
