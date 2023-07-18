package com.override.orchestrator_service.model;

import com.override.dto.constants.Type;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "categories",
        uniqueConstraints =
                {@UniqueConstraint
                        (name = "UniqueAccIdAndCategoryName", columnNames = {"name", "account_id"})
                })
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Type type;

    @OneToMany(mappedBy = "category")
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private Set<Keyword> keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    private OverMoneyAccount account;

    public Category(String name, Type type, OverMoneyAccount account) {
        this.name = name;
        this.type = type;
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
