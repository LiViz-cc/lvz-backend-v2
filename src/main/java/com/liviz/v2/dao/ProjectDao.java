package com.liviz.v2.dao;

import com.liviz.v2.model.Project;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectDao extends MongoRepository<Project, String> {

    /**
     * Find project by id and user id
     *
     * @param id     project id
     * @param userId user id
     * @return Optional<Project>
     */
    @Query("{id: ?0}, {'createdBy.id': ?1 }")
    Optional<Project> findByIdAndUserId(String id, String userId);

    @Query("{isPublic: ?0}, {'createdBy.id': ?1 }")
    List<Project> queryByFilters(Boolean isPublic, String createdById);

    @Query("{isPublic: ?0}")
    List<Project> queryByIsPublic(Boolean isPublic);

    @Query("{'createdBy.id': ?0}")
    List<Project> queryByCreatedBy(String createdById);

    @DeleteQuery("{'createdBy.id': ?0}")
    void deleteAllByUserId(String userId);
}
