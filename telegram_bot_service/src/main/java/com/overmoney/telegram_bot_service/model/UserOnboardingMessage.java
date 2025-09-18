package com.overmoney.telegram_bot_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_onboarding_messages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "onboarding_message_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOnboardingMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_message_id")
    private OnboardingMessage onboardingMessage;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;
}
