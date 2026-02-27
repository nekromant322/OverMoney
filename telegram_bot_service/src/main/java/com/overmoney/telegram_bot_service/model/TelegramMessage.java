package com.overmoney.telegram_bot_service.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "telegram_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Поле id сущности TelegramMessage соответствует id сообщения в телеграмме
     */
    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "id_transaction")
    private UUID idTransaction;
}
