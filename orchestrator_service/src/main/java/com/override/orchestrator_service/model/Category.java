package com.override.orchestrator_service.model;

import com.override.orchestrator_service.constants.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="categories")
@RequiredArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Type type;

    @OneToMany(mappedBy="category")
    private Set<Transaction> transactions;

    @OneToMany(mappedBy="category")
    private Set<Keyword> keywords;

    @ManyToOne
    private User user;
}
