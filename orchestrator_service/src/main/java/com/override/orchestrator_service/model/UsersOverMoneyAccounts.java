package com.override.orchestrator_service.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users_over_money_accounts")
@Getter
@Setter
@RequiredArgsConstructor
public class UsersOverMoneyAccounts {
    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(unique = true)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "over_money_accounts_id")
    private OverMoneyAccount overMoneyAccount;
}
