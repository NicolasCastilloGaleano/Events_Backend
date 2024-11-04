package com.events.security.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.events.security.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{'email': ?0}")
    public Optional<User> getUserByEmail(String email);

    @Query(value = "{}", fields = "{'_id': 1, 'email': 1,'role':1}")
    List<User> findAllProjectedBy();
}
