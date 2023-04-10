package com.liviz.v2.DataSource;

import com.liviz.v2.DataSource.DataSourceDao;
import com.liviz.v2.DataSource.DataSourceDto;
import com.liviz.v2.DataSource.DataSourceResponseDto;
import com.liviz.v2.exception.BadRequestException;
import com.liviz.v2.exception.NoSuchElementFoundException;
import com.liviz.v2.DataSource.DataSource;
import com.liviz.v2.User.User;
import com.liviz.v2.ApiFetch.ApiFetcher;
import com.liviz.v2.DataSource.DataSourceService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class DataSourceServiceImpl implements DataSourceService {
    @Autowired
    DataSourceDao dataSourceDao;

    @Autowired
    ApiFetcher apiFetchService;

    @Override
    @NotNull
    public DataSource createDataSource(DataSourceDto dataSourceDto, User user) {
        // create new data source
        DataSource dataSource =
                new DataSource(dataSourceDto.getName(), dataSourceDto.getIsPublic(), dataSourceDto.getDescription(),
                        dataSourceDto.getStaticData(), dataSourceDto.getDataType(), dataSourceDto.getUrl(), dataSourceDto.getDataSourceExample(),
                        dataSourceDto.getDataSourceSlots());
        dataSource.setCreatedBy(user);

        // save data source
        dataSource = dataSourceDao.save(dataSource);
        return dataSource;
    }

    @Override
    public @NotNull List<DataSource> findAllByFilters(User user, String createdById, Boolean isPublic) {
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

    @Override
    public void deleteDataSource(String dataSourceId, User user) {
        // get data source
        Optional<DataSource> dataSourceOptional = dataSourceDao.findByIdAndUserId(dataSourceId, user.getId());

        // delete data source
        dataSourceDao.delete(dataSourceOptional.orElseThrow(
                () -> new NoSuchElementFoundException(String.format("Data source not found with id %s and current user", dataSourceId)))
        );
    }

    @Override
    public @NotNull DataSource cloneDataSource(String id, User user) {
        // get data source by id
        Optional<DataSource> dataSourceOptional = dataSourceDao.findById(id);

        if (dataSourceOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Data source not found with id %s and current user", id));
        }
        DataSource dataSource = dataSourceOptional.get();

        // check if data source is public or created by user
        if (dataSource.getIsPublic() != Boolean.TRUE && !dataSource.getCreatedBy().getId().equals(user.getId())) {
            throw new BadRequestException("Access to other users' non-public data sources is not allowed.");
        }

        // clone data source
        DataSource clonedDataSource = new DataSource(dataSource);

        // set id
        clonedDataSource.setId(null);

        // set created by
        clonedDataSource.setCreatedBy(user);

        // set time
        clonedDataSource.setCreatedTime(new Date());
        clonedDataSource.setModifiedTime(new Date());

        // clean linkage
        clonedDataSource.setProjects(new ArrayList<>());

        // save cloned data source
        return dataSourceDao.save(clonedDataSource);
    }

    @Override
    public @NotNull DataSource updateDataSource(String id, DataSourceDto dataSourceDto, User user) {
        // get data source by id and userId
        Optional<DataSource> dataSourceOptional = dataSourceDao.findByIdAndUserId(id, user.getId());

        if (dataSourceOptional.isEmpty()) {
            throw new NoSuchElementFoundException(String.format("Data source not found with id %s and current user", id));
        }

        // get data source
        DataSource dataSource = dataSourceOptional.get();

        // update data source fields
        dataSource.setName(dataSourceDto.getName());
        dataSource.setIsPublic(dataSourceDto.getIsPublic());
        dataSource.setDescription(dataSourceDto.getDescription());
        dataSource.setStaticData(dataSourceDto.getStaticData());
        dataSource.setDataType(dataSourceDto.getDataType());
        dataSource.setUrl(dataSourceDto.getUrl());
        dataSource.setDataSourceExample(dataSourceDto.getDataSourceExample());
        dataSource.setDataSourceSlots(dataSourceDto.getDataSourceSlots());

        // update modified time
        dataSource.setModifiedTime(new Date());

        // save data source
        return dataSourceDao.save(dataSource);
    }

    @Override
    @NotNull
    public DataSourceResponseDto getDataSource(String id, User user, Map<String, String> requestParams) throws IOException, URISyntaxException {
        // return not found if data source is not found
        Optional<DataSource> dataSourceData = dataSourceDao.findByIdAndUserId(id, user.getId());
        if (dataSourceData.isEmpty()) {
            throw new NoSuchElementFoundException("Data source with id " + id + " not found");
        }

        // get data source
        DataSource dataSource = dataSourceData.get();

        // return data source without data if data is not requested
        if (requestParams.isEmpty()) {
            return new DataSourceResponseDto(dataSourceData.get());
        }

        // get data
        var node = apiFetchService.fetchData(dataSource.getUrl(), dataSource.getDataSourceSlots(), requestParams);

        return new DataSourceResponseDto(dataSourceData.get(), node);
    }
}
