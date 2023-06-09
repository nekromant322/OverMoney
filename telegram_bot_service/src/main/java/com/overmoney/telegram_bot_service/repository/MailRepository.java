package com.overmoney.telegram_bot_service.repository;

import com.overmoney.telegram_bot_service.model.Announce;
import com.overmoney.telegram_bot_service.model.Mail;
import com.override.dto.MailDTO;
import com.override.dto.constants.StatusMailing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<Mail, Long> {

    @Query("SELECT m FROM Mail m WHERE m.statusMailing= :statusMailing AND m.announce= :announce")
    List<Mail> findAllMailsByStatusMailing(@Param("statusMailing") StatusMailing statusMailing,
                                           @Param("announce") Announce announce);

    @Query("SELECT new com.override.dto.MailDTO(m.statusMailing, COUNT(m.statusMailing))\n" +
            "FROM Mail m \n" +
            "WHERE m.announce = :announce\n" +
            "GROUP BY m.statusMailing")
    List<MailDTO> getCountOfMailsStatusByAnnounce(@Param("announce") Announce announceId);
}