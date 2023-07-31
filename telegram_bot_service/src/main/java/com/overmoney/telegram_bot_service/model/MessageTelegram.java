package com.overmoney.telegram_bot_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "message_telegram")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageTelegram {

    @Id
    private Integer id;

    @Column(name = "id_transaction")
    private UUID idTransaction;
}
