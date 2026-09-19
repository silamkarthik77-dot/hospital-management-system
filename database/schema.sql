-- ============================================================
-- Hospital Management System - MySQL Schema
-- Normalized to 3NF. Run this before seed_data.sql.
-- Compatible with MySQL 8.x and AWS RDS for MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS hms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hms_db;

-- ---------- Roles ----------
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
    created_at DATETIME(6),
    updated_at DATETIME(6)
);

-- ---------- Users (central identity table) ----------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role_id BIGINT NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    reset_password_token VARCHAR(255),
    reset_password_token_expiry DATETIME(6),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    INDEX idx_users_email (email)
);

-- ---------- Departments ----------
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    created_at DATETIME(6),
    updated_at DATETIME(6)
);

-- ---------- Doctors ----------
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    specialization VARCHAR(100),
    license_number VARCHAR(50),
    experience_years INT,
    consultation_fee DECIMAL(10,2),
    bio VARCHAR(1000),
    available_days VARCHAR(50),
    slot_start_time VARCHAR(20),
    slot_end_time VARCHAR(20),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_doctors_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_doctors_department FOREIGN KEY (department_id) REFERENCES departments(id)
);

-- ---------- Patients ----------
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    gender VARCHAR(10),
    blood_group VARCHAR(10),
    address VARCHAR(300),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    medical_history VARCHAR(1000),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_patients_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ---------- Appointments ----------
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    reason_for_visit VARCHAR(500),
    diagnosis_notes VARCHAR(2000),
    cancellation_reason VARCHAR(500),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_appt_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_appt_doctor FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    INDEX idx_appt_doctor_date (doctor_id, appointment_date),
    INDEX idx_appt_patient (patient_id)
);

-- ---------- Prescriptions ----------
CREATE TABLE prescriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL UNIQUE,
    medicines VARCHAR(2000),
    instructions VARCHAR(1000),
    file_s3_key VARCHAR(500),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_presc_appt FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
);

-- ---------- Medical Reports (metadata; files live in S3) ----------
CREATE TABLE medical_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    uploaded_by_doctor_id BIGINT,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    s3_key VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size_bytes BIGINT,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_report_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_report_doctor FOREIGN KEY (uploaded_by_doctor_id) REFERENCES doctors(id)
);

-- ---------- Invoices ----------
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(40) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT,
    bill_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(10,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    pdf_s3_key VARCHAR(500),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_invoice_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
    CONSTRAINT fk_invoice_appt FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    INDEX idx_invoice_status (status)
);

-- ---------- Payments ----------
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30),
    transaction_reference VARCHAR(100),
    paid_at DATETIME(6),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_payment_invoice FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- ---------- Notifications ----------
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(1000),
    is_read BOOLEAN DEFAULT FALSE,
    type VARCHAR(40),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
