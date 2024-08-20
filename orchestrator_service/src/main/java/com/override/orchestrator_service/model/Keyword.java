package com.override.orchestrator_service.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @Column(name = "usage_count")
    private int usageCount;

    public Keyword(KeywordId keywordId, Category category) {
        this.keywordId = keywordId;
        this.category = category;
        this.usageCount = 0;
    }

    public void incrementUsageCount() {
        this.usageCount++;
    }
}