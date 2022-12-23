package com.liviz.v2.dao;

import com.liviz.v2.model.DisplaySchema;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DisplaySchemaDao extends MongoRepository<DisplaySchema, String> {
}

