package com.liviz.v2.DataSource;

import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DataSourceExample {
    public HashMap<String, String> params;
    public HashMap<String, String> data;

    public DataSourceExample(DataSourceExample other) {
        this.params = other.params;
        this.data = other.data;
    }
}
