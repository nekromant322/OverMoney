package com.override.orchestrator_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "suggestions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Suggestion {
    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(unique = true, columnDefinition = "UUID")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(name = "suggested_category_id")
    private Long suggestedCategoryId;

    @Column
    private Float accuracy;

    @Column
    private boolean isCorrect;

    @Column
    private String algorithm;
}