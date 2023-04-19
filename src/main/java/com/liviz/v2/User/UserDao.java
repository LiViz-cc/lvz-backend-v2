package com.liviz.v2.User;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserDao extends MongoRepository<User, String> {

    @Query("{ 'email' : ?0 }")
    Optional<User> findByEmail(String email);

    @Query("{ 'username' : ?0 }")
    Optional<User> findByUsername(String username);

}
