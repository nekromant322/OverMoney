package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.Announce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnounceRepository extends JpaRepository<Announce, Long> {
    Optional<Announce> findTopByOrderByIdDesc();
}
