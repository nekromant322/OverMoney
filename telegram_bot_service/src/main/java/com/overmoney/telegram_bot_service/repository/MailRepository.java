package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {
}
