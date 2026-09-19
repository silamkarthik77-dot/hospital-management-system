package com.hms.repository;

import com.hms.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoice_Id(Long invoiceId);
}
