package com.liviz.v2.DataSource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class DataSourceResponseDto extends DataSource {

    protected JsonNode data;

    public DataSourceResponseDto(DataSource dataSource) {
        super(dataSource);
    }

    public DataSourceResponseDto(DataSource dataSource, JsonNode data) {
        super(dataSource);
        this.data = data;
    }
}
