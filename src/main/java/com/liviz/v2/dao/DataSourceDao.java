package com.liviz.v2.dao;

import com.liviz.v2.model.DataSource;
import com.liviz.v2.model.Project;
import com.liviz.v2.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DataSourceDao extends MongoRepository<DataSource, String> {
    @Query("{ isPublic: ?0 }, { createdBy: { id: ?1 } }")
    List<DataSource> queryByFilters(Boolean isPublic, String createdById);

    @Query("{ isPublic: ?0 }")
    List<DataSource> queryByIsPublic(Boolean isPublic);

    @Query("{ createdBy: { id: ?0 } }")
        // TODO: bug?
    List<DataSource> queryByCreatedBy(String createdById);
}
