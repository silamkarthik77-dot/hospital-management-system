package com.hms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prescriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(length = 2000)
    private String medicines;

    @Column(length = 1000)
    private String instructions;

    /** S3 object key for the uploaded prescription document/scan, if any. */
    private String fileS3Key;
}
