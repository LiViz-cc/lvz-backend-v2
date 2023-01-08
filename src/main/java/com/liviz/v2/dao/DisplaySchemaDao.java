package com.liviz.v2.dao;

import com.liviz.v2.model.DisplaySchema;
import com.liviz.v2.model.User;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface DisplaySchemaDao extends MongoRepository<DisplaySchema, String> {
    @Query("{id: ?0}, {'createdBy.id': ?1 }")
    Optional<DisplaySchema> findByIdAndUserId(String displaySchemaId, String userId);

    @DeleteQuery("{'user.id': ?0 }")
    void deleteAllByUserId(String userId);
}

