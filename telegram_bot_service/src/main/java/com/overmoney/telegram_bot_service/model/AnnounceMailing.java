package com.overmoney.telegram_bot_service.model;

import com.override.dto.constants.StatusMailing;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announse_mailing")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AnnounceMailing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "status_mailing")
    private StatusMailing statusMailing;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime date;
}
