package com.override.orchestrator_service.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="keywords")
@Getter
@Setter
public class Keyword {

    @Id
    private Long id;

    @Column
    private String keyword;

    @ManyToOne
    private Category category;

}
