package com.hms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(length = 100)
    private String specialization;

    @Column(length = 50)
    private String licenseNumber;

    private Integer experienceYears;

    private BigDecimal consultationFee;

    @Column(length = 1000)
    private String bio;

    // Example: MON,TUE,WED,THU,FRI
    @Column(length = 50)
    private String availableDays;

    @Column(length = 20)
    private String slotStartTime;

    @Column(length = 20)
    private String slotEndTime;
}