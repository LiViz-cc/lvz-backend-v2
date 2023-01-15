package com.liviz.v2.service;

import com.liviz.v2.dto.DataSourceDto;
import com.liviz.v2.dto.DataSourceResponseDto;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public interface DataSourceService {
    @NotNull DataSource createDataSource(DataSourceDto dataSourceDto, User user);

    @NotNull List<DataSource> findAllByFilters(User user, String createdById, Boolean isPublic);

    void deleteDataSource(String dataSourceId, User user);

    @NotNull DataSource cloneDataSource(String id, User user);

    @NotNull DataSource updateDataSource(String id, DataSourceDto dataSourceDto, User user);

    @NotNull DataSourceResponseDto getDataSource(String id, User user, Map<String, String> requestParams) throws IOException, URISyntaxException;
}
