package com.overmoney.telegram_bot_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import javax.persistence.*;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "onboarding_messages")
public class OnboardingMessage {
    @Id
    @Column(unique = true, columnDefinition = "UUID")
    private UUID id;
    @Column(name = "message")
    private String message;
    @Lob
    @Column(name = "image", columnDefinition = "bytea")
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] image;
    @Column(name = "day_delay")
    private Integer dayDelay;
}
