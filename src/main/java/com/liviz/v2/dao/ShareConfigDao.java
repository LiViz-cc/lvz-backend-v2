package com.liviz.v2.dao;

import com.liviz.v2.model.ShareConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShareConfigDao extends MongoRepository<ShareConfig, String> {

}
