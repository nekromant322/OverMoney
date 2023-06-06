package com.override.orchestrator_service.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "keywords")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Keyword {

    @EmbeddedId
    private KeywordId keywordId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
}