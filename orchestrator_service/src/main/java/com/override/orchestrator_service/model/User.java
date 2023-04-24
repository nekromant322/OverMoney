package com.override.orchestrator_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
