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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private Set<Category> categories;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "account")
    private Set<Transaction> transactions;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_accounts",
            joinColumns = @JoinColumn(name = "accounts_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private Set<User> users;
}
