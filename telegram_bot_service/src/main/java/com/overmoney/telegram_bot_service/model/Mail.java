package com.overmoney.telegram_bot_service.model;

import com.override.dto.constants.StatusMailing;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "mails")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Mail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_tg_id")
    private Long userTgId;

    @Column(name = "status_mailing")
    private StatusMailing statusMailing;

    @ManyToOne
    private Announce announce;
}
