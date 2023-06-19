package com.overmoney.telegram_bot_service.model;

import lombok.*;

import javax.persistence.*;

/**
 * @author etozhealexis
 * Сущность для записи id сообщения с запросом на перенос данных в личном чате
 * для последующего удаления сообщения
 */
@Entity
@Table(name = "merge_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MergeRequest {

    /**
     * Поле, содержащее сгенерированный id запроса на перенос данных
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Поле, содержащее id чата телеграма с отправленным запросом
     * на перенос данных
     */
    @Column(name = "chat_id")
    private Long chatId;

    /**
     * Поле, содержащее id сообщения запроса на перенос данных
     * (необходимо для последующего удаления сообщения)
     */
    @Column(name = "message_id")
    private Integer messageId;

    /**
     * Поле, отображающее завершенность переноса данных
     */
    @Column(name = "completed")
    private Boolean completed;
}