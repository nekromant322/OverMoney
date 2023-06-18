package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.AnnounceMailing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnounceMailingRepository extends JpaRepository<AnnounceMailing, Long> {
}
