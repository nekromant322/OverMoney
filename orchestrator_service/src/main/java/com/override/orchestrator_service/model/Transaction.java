package com.override.orchestrator_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="transactions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(unique = true)
    private UUID id;

    @Column
    private String message;

    @ManyToOne
    @Nullable
    private Category category;

    @Column
    private Float amount;

    @Column(columnDefinition = "DATE")
    private Date date;

    @ManyToOne
    private OverMoneyAccount account;
}
