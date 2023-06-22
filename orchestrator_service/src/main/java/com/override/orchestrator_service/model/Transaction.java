package com.override.orchestrator_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(unique = true, columnDefinition = "UUID")
    private UUID id;

    @Column
    private String message;

    @ManyToOne
    @Nullable
    private Category category;

    @Column
    private Float amount;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime date;

    @Column(name = "suggested_category_id")
    @Nullable
    private Long suggestedCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    private OverMoneyAccount account;

    @Column(name = "telegram_user_id")
    @Nullable
    private Long telegramUserId;
}
