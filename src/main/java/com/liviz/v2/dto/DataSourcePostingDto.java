package com.liviz.v2.dto;

import com.liviz.v2.model.User;

public class DataSourcePostingDto {
    private String name;
    private boolean isPublic;
    private String description;

    private String static_data;

    private String data_type;

    private String url;

    public DataSourcePostingDto(String name, boolean isPublic, String description, String static_data, String data_type, String url) {
        this.name = name;
        this.isPublic = isPublic;
        this.description = description;
        this.static_data = static_data;
        this.data_type = data_type;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public String getDescription() {
        return description;
    }

    public String getStatic_data() {
        return static_data;
    }

    public String getData_type() {
        return data_type;
    }

    public String getUrl() {
        return url;
    }
}
