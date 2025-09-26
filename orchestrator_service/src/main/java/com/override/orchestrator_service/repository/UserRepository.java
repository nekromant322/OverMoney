package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT COUNT(*) FROM users", nativeQuery = true)
    int getUsersCount();

    @Query("select u from User u where u.username = :username")
    User findByUsername(@Param("username") String username);

    @Query("select u.id from User u")
    Set<Long> getAllUserIds();

    @Query("select u from User u where u.id in (:ids)")
    List<User> findAllUsersByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE User u SET u.username = :username, u.firstName = :firstName, " +
            "u.lastName = :lastName, u.authDate = :authDate, " +
            "u.registrationDate = :registrationDate " +
            "WHERE u.id= :userId")
    void updateUserDetailsByUserId(@Param("userId") Long userId,
                                   @Param("username") String username,
                                   @Param("firstName") String firstName,
                                   @Param("lastName") String lastName,
                                   @Param("authDate") String authDate,
                                   @Param("registrationDate") LocalDateTime registrationDate);

    @Query("select u from User u where u.id not in :ids and u.registrationDate is not null ")
    List<User> findByNotInAndRegistrationDateIsNotNull(@Param("ids") Set<Long> ids);

    List<User> findByRegistrationDateIsNotNull();
}
