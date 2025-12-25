package com.override.payment_service.repository;

import com.override.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByInvoiceId(Long invoiceId);
}
