package com.override.orchestrator_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column
    private String username;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String photoUrl;

    @Column
    private String authDate;

    @Column
    private LocalDateTime registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private OverMoneyAccount account;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "profile_photo_id", referencedColumnName = "id")
    private ProfilePhoto profilePhoto;
}
