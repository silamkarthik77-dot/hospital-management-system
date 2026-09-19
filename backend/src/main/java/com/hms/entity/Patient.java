package com.hms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User user;

    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender;

    @Column(length = 10)
    private String bloodGroup;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String emergencyContactName;

    @Column(length = 20)
    private String emergencyContactPhone;

    @Column(length = 1000)
    private String medicalHistory;
}