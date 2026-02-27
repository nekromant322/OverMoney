package com.override.payment_service.repository;

import com.override.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByInvoiceId(Long invoiceId);
}
