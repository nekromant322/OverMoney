package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.username = :username")
    User findByUsername(@Param("username") String username);

    @Query("select u.id from User u")
    Set<Long> getAllUserIds();
}
