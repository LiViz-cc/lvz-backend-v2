package com.liviz.v2.service;

import com.liviz.v2.dao.DataSourceDao;
import com.liviz.v2.dao.DisplaySchemaDao;
import com.liviz.v2.dao.ProjectDao;
import com.liviz.v2.dto.DataSourceDto;
import com.liviz.v2.dto.DisplaySchemaDto;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class DataSourceService {
    @Autowired
    DataSourceDao dataSourceDao;

    @NotNull
    public DataSource createDataSource(DataSourceDto dataSourceDto, User user) {
        // create new data source
        DataSource dataSource =
                new DataSource(dataSourceDto.getName(), dataSourceDto.getIsPublic(), dataSourceDto.getDescription(),
                        dataSourceDto.getStaticData(), dataSourceDto.getDataType(), dataSourceDto.getUrl(),
                        dataSourceDto.getSlots());
        dataSource.setCreatedBy(user);

        // save data source
        dataSource = dataSourceDao.save(dataSource);
        return dataSource;
    }

}
