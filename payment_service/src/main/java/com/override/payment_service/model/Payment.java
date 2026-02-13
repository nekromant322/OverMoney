package com.override.payment_service.model;

import com.override.dto.constants.Currency;
import com.override.dto.constants.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@Accessors(chain = true)
@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoiceId")
    private Long invoiceId;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "cleanAmount")
    private BigDecimal cleanAmount;
    @Column(name = "currency")
    @Enumerated(value = EnumType.STRING)
    private Currency currency;
    @Column(name = "email")
    private String email;
    @Column(name = "description")
    private String description;
    @Column(name = "paymentStatus")
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;
}
