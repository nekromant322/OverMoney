package com.override.orchestrator_service.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="transactions")
@Getter
@Setter
public class Transaction {

    @Id
    private Long id;

    @Column
    private String message;

    @ManyToOne
    private Category category;

    @Column
    private Long amount;

    @ManyToOne
    private User user;
}
