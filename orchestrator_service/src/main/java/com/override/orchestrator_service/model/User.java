package com.override.orchestrator_service.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String photoUrl;

    @Column
    private String authDate;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Category> categories;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Transaction> transactions;
}
