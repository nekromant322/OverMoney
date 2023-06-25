package com.override.orchestrator_service.repository;

import com.override.orchestrator_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.username = :username")
    User findByUsername(@Param("username") String username);

    @Query("select u.id from User u")
    Set<Long> getAllUserIds();

    @Query("select u from User u where u.id in (:ids)")
    List<User> findAllUsersByIds(@Param("ids") List<Long> ids);

    @Modifying
    @Query("UPDATE User u SET u.username = :username, u.firstName = :firstName, " +
            "u.lastName = :lastName, u.photoUrl = :photoUrl, u.authDate = :authDate " +
            "WHERE u.id= :userId")
    void updateUserDetailsByUserId(@Param("userId") Long userId,
                                   @Param("username") String username,
                                   @Param("firstName") String firstName,
                                   @Param("lastName") String lastName,
                                   @Param("photoUrl") String photoUrl,
                                   @Param("authDate") String authDate);
}
