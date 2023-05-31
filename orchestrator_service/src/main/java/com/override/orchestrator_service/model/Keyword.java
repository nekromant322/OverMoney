package com.override.orchestrator_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "keywords")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(unique = true)
    private UUID id;

    @Column
    private String keyword;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    public Keyword(String keyword, Category category) {
        this.keyword = keyword;
        this.category = category;
    }

    public Keyword(String keyword) {
        this.keyword = keyword;
    }
}