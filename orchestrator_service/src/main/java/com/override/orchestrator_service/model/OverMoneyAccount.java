package com.override.orchestrator_service.model;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "over_money_accounts")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OverMoneyAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id")
    private String chatId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "overMoneyAccount")
    private Set<Category> categories;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "overMoneyAccount")
    private Set<Transaction> transactions;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "overMoneyAccount")
    private Set<UsersOverMoneyAccounts> usersOverMoneyAccounts;
}
