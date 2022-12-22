package com.liviz.v2.dao;

import com.liviz.v2.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectDao extends MongoRepository<Project, String> {

}
