package com.liviz.v2.dao;

import com.liviz.v2.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ProjectDao extends MongoRepository<Project, String> {

    /**
     * Find project by id and user id
     * @param id project id
     * @param userId user id
     * @return Optional<Project>
     */
    @Query("{id: ?0}, {user: {id: ?1} }")
    Optional<Project> findByIdAndUserId(String id, String userId);
}
