package com.override.orchestrator_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Role implements GrantedAuthority {

    @Id
    private Long id;

    @Column
    private String role;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public Role(long id, String role) {
        this.id = id;
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role;
    }
}
