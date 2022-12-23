package com.liviz.v2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DataSourceExample {

    public HashMap<String,String> params;
    public HashMap<String,String> data;

    public DataSourceExample(DataSourceExample other) {
        this.params = other.params;
        this.data = other.data;
    }
}
