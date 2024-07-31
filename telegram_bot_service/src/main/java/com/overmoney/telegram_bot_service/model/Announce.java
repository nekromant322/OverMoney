package com.overmoney.telegram_bot_service.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "announcements")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Announce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "text_announce")
    private String textAnnounce;

    @Column
    @OneToMany(mappedBy = "announce")
    private Set<Mail> mails;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime date;
}
