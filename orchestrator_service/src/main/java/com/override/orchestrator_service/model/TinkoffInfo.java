package com.override.orchestrator_service.model;

import lombok.*;

import javax.annotation.Nullable;
import javax.persistence.*;

@Entity
@Table(name = "tinkoff_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TinkoffInfo {

    @Id
    private Long id;

    @Column
    private String token;

    @Column
    @Nullable
    private Long favoriteAccountId;

    @OneToOne
    @MapsId
    @JoinColumn
    private OverMoneyAccount account;
}

