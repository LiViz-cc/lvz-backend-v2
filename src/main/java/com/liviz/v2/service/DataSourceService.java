package com.liviz.v2.service;

import com.liviz.v2.dao.DataSourceDao;
import com.liviz.v2.dto.DataSourceDto;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<DataSource> findAllByFilters(User user, String createdById, Boolean isPublic) {
        // if requested user is not the jwt user
        if (user == null || !user.getId().equals(createdById)) {
            // if isPublic is not True
            if (isPublic == null || !isPublic) {
                throw new BadRequestException("Access to other users' non-public data sources is not allowed.");
            }
        }

        if (isPublic != null && createdById != null) {
            return dataSourceDao.queryByFilters(isPublic, createdById);
        } else if (isPublic != null) {
            return dataSourceDao.queryByIsPublic(isPublic);
        } else if (createdById != null) {
            return dataSourceDao.findByCreatedBy_Id(createdById);
        }

        throw new BadRequestException("This query combination is not allowed.");

    }
}
