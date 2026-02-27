package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRegistrationDateBefore(LocalDateTime date);

    @Query("SELECT u.id FROM User u")
    Set<Long> findAllIdsBy();
}
