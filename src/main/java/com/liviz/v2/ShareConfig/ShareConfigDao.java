package com.liviz.v2.ShareConfig;

import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShareConfigDao extends MongoRepository<ShareConfig, String> {

    @Query("{id: ?0}, {'createdBy.id': ?1 }")
    Optional<ShareConfig> findByIdAndUserId(String id, String userId);

    @DeleteQuery("{'createdBy.id': ?0 }")
    void deleteAllByUserId(String userId);

    @Query("{ 'createdBy.id': ?0 }")
    List<ShareConfig> queryByCreatedBy(String createdById);
}
