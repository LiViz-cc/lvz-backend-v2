package com.liviz.v2.DataSource;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DataSourceDao extends MongoRepository<DataSource, String> {
    @Query("{ isPublic: ?0 }, { 'createdBy.id': ?1 }")
    List<DataSource> queryByFilters(Boolean isPublic, String createdById);

    @Query("{ isPublic: ?0 }")
    List<DataSource> queryByIsPublic(Boolean isPublic);

    @Query("{ 'createdBy.id': ?0 } }")
    List<DataSource> findByCreatedBy_Id(String createdById);

    @Query("{ 'id': ?0, 'createdBy.id': ?1 } }")
    Optional<DataSource> findByIdAndUserId(String id, String userId);

    @DeleteQuery("{'createdBy.id': ?0 } }")
    void deleteAllByUserId(String userId);
}
