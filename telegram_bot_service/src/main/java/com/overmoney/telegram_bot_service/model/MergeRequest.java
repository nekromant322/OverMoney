package com.overmoney.telegram_bot_service.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "merge_requests")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MergeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "completed")
    private Boolean completed;
}
