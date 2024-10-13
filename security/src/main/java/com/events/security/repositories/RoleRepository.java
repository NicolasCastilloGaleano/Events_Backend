package com.events.security.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.events.security.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    @Query("{'name':?0}")
    Optional<Role> getRole(String name);
}
