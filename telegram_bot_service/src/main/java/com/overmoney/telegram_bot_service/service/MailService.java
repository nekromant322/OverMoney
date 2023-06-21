package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.model.Announce;
import com.overmoney.telegram_bot_service.model.Mail;
import com.overmoney.telegram_bot_service.repository.MailRepository;
import com.override.dto.constants.StatusMailing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {

    @Autowired
    private MailRepository mailRepository;

    public Mail createMail(Announce announce, StatusMailing statusMailing, long userTgId) {
        return Mail.builder()
                .announce(announce)
                .statusMailing(statusMailing)
                .userTgId(userTgId)
                .build();
    }

    public void saveListOfMails(List<Mail> mails) {
        mailRepository.saveAll(mails);
    }

    public List<Mail> findAllMails() {
        return mailRepository.findAll();
    }

    public List<Mail> findAllMailsByStatus(StatusMailing statusMailing) {
        return mailRepository.findAllMailsByStatusMailing(statusMailing);
    }
}
