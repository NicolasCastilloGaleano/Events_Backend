package com.events.security.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.events.security.models.Permission;

public interface PermissionRepository extends MongoRepository<Permission, String> {
    @Query("{'route':?0,'method':?1}")
    Optional<Permission> getPermission(String route, String method);
}
