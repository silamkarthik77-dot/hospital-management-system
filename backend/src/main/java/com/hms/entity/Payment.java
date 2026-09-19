package com.hms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    @Column(length = 30)
    private String paymentMethod; // CARD, CASH, UPI, NET_BANKING

    @Column(length = 100)
    private String transactionReference;

    private LocalDateTime paidAt;
}
