package com.hms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medical_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_doctor_id")
    private Doctor uploadedByDoctor;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String fileName;

    private Long fileSizeBytes;
}
