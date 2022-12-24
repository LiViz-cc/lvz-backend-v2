package com.liviz.v2.dao;

import com.liviz.v2.model.ShareConfig;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ShareConfigDao extends MongoRepository<ShareConfig, String> {

    @Query("{id: ?0}, {user: {id: ?1} }")
    Optional<ShareConfig> findByIdAndUserId(String id, String userId);

}
