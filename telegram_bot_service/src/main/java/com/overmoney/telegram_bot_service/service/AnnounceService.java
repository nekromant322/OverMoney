package com.overmoney.telegram_bot_service.service;

import com.overmoney.telegram_bot_service.OverMoneyBot;
import com.overmoney.telegram_bot_service.model.Announce;
import com.overmoney.telegram_bot_service.model.Mail;
import com.overmoney.telegram_bot_service.repository.AnnounceRepository;
import com.override.dto.constants.StatusMailing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Service
public class AnnounceService {

    @Autowired
    private OverMoneyBot overMoneyBot;

    @Autowired
    private MailService mailService;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private ExecutorService executorService;

    @Value("${max-mailing-messages.maxMessagesOfAnnouncePerSecond}")
    private long MAX_MESSAGES_PER_SECOND;

    public void sendAnnounce(String announceText, Set<Long> userIds) {
        Announce announce = saveAnnounce(announceText);
        List<Mail> mails = new ArrayList<>();
        for (Long userTgId : userIds) {
            mails.add(mailService.createMail(announce, StatusMailing.PENDING, userTgId));
        }
        mailService.saveListOfMails(mails);
        sendAnnounceForUsersInPending(announceText, announce);
    }

    private void sendAnnounceForUsersInPending(String announceText, Announce announce) {
        executorService.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Mail> mailsInPending = mailService.findAllMailsByStatusAndAnnounce(StatusMailing.PENDING, announce);
            long countMessage = 0;
            for (Mail mail : mailsInPending) {
                long userTgId = mail.getUserTgId();
                if (countMessage < MAX_MESSAGES_PER_SECOND) {
                    StatusMailing statusMailing = overMoneyBot.sendMessage(userTgId, announceText);
                    mail.setStatusMailing(statusMailing);
                    countMessage++;
                } else {
                    try {
                        mailService.saveListOfMails(mailsInPending);
                        Thread.sleep(1000);
                        StatusMailing statusMailing = overMoneyBot.sendMessage(userTgId, announceText);
                        mail.setStatusMailing(statusMailing);
                        countMessage = 1;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            mailService.saveListOfMails(mailsInPending);
        });
    }

    public Announce saveAnnounce(String announceText) {
        return announceRepository.save(Announce.builder()
                .textAnnounce(announceText)
                .date(LocalDateTime.now())
                .build());
    }
}
