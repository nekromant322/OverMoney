package com.override.orchestrator_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "profile_photos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfilePhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "profile_photo", columnDefinition = "bytea")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] photoData;

    @OneToOne(mappedBy = "profilePhoto")
    private User user;
}
