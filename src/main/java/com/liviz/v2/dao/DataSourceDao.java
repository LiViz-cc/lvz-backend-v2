package com.liviz.v2.dao;

import com.liviz.v2.model.DataSource;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DataSourceDao extends MongoRepository<DataSource, String> {
}
