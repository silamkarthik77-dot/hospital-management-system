package com.hms.repository;

import com.hms.entity.Invoice;
import com.hms.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPatient_Id(Long patientId);
    List<Invoice> findByStatus(PaymentStatus status);

    @org.springframework.data.jpa.repository.Query(
        "select coalesce(sum(i.totalAmount), 0) from Invoice i where i.status = 'PAID' and i.updatedAt between :start and :end")
    java.math.BigDecimal sumRevenueBetween(LocalDateTime start, LocalDateTime end);
}
