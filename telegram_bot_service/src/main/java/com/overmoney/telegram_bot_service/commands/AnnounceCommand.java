package com.overmoney.telegram_bot_service.commands;

import com.overmoney.telegram_bot_service.model.Announce;
import com.overmoney.telegram_bot_service.model.Mail;
import com.overmoney.telegram_bot_service.repository.AnnounceRepository;
import com.overmoney.telegram_bot_service.service.MailService;
import com.overmoney.telegram_bot_service.service.TelegramMessageService;
import com.overmoney.telegram_bot_service.util.ValidationUtils;
import com.override.dto.constants.StatusMailing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static com.overmoney.telegram_bot_service.constants.Command.ANNOUNCE;
import static com.overmoney.telegram_bot_service.constants.MessageConstants.NO_RIGHTS;

@Component
@Slf4j
public class AnnounceCommand extends OverMoneyCommand {
    @Value("${admin.allowed_users}")
    private Set<String> admins;
    @Autowired
    private MailService mailService;
    @Autowired
    private TelegramMessageService telegramMessageService;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private ExecutorService executorService;

    @Value("${max-mailing-messages.maxMessagesOfAnnouncePerSecond}")
    private long MAX_MESSAGES_PER_SECOND;

    @Autowired
    public AnnounceCommand() {
        super(ANNOUNCE.getAlias(), ANNOUNCE.getDescription());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] args) {
        SendMessage message = new SendMessage();
        String chatId = chat.getId().toString();
        if (!admins.contains(user.getUserName())) {
            message.setChatId(chatId);
            message.setText(NO_RIGHTS);
            execute(absSender, message, user);
            return;
        }
        ValidationUtils.validateArguments(args);
        String announce = String.join(" ", args);
        Set<Long> userIds = telegramMessageService.getAllUniqChatIds();
        sendAnnounce(announce, userIds, absSender);
    }

    public void sendAnnounce(String announceText, Set<Long> userIds, AbsSender absSender) {
        Announce announce = saveAnnounce(announceText);
        List<Mail> mails = new ArrayList<>();
        for (Long userTgId : userIds) {
            mails.add(mailService.createMail(announce, StatusMailing.PENDING, userTgId));
        }
        mailService.saveListOfMails(mails);
        sendAnnounceForUsersInPending(announceText, announce, absSender);
    }

    private void sendAnnounceForUsersInPending(String announceText, Announce announce, AbsSender absSender) {
        executorService.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<Mail> mailsInPending = mailService.findAllMailsByStatusAndAnnounce(StatusMailing.PENDING, announce);
            if (mailsInPending.isEmpty()) {
                log.info("pusto");
            }
            for (int i = 0; i < mailsInPending.size(); i++) {
                log.info("pochta " + mailsInPending.get(i));
            }
            long countMessage = 0;
            for (Mail mail : mailsInPending) {
                log.info("Готовим почту");
                long userTgId = mail.getUserTgId();
                if (countMessage < MAX_MESSAGES_PER_SECOND) {
                    StatusMailing statusMailing = sendMessage(absSender, userTgId, announceText);
                    mail.setStatusMailing(statusMailing);
                    countMessage++;
                } else {
                    try {
                        mailService.saveListOfMails(mailsInPending);
                        Thread.sleep(1000);
                        StatusMailing statusMailing = sendMessage(absSender, userTgId, announceText);
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
