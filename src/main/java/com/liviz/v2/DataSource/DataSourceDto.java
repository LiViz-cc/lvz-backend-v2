package com.liviz.v2.DataSource;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataSourceDto {
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

    private DataSourceExample dataSourceExample;

    @JsonSetter(nulls = Nulls.SKIP)
    private List<DataSourceSlot> dataSourceSlots = new ArrayList<>();


}
