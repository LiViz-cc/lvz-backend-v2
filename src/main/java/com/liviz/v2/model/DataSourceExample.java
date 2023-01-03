package com.liviz.v2.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.annotation.Id;

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
