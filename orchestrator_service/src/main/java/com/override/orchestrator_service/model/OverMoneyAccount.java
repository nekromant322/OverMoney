package com.override.orchestrator_service.model;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OverMoneyAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", unique = true)
    private Long chatId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private Set<Category> categories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private Set<Transaction> transactions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private Set<User> users;
}
