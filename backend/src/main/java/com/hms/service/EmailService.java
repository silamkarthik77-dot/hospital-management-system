package com.hms.service;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String body);
    void sendRegistrationEmail(String to, String fullName);
    void sendAppointmentConfirmationEmail(String to, String patientName, String doctorName, String date, String time);
    void sendAppointmentCancellationEmail(String to, String patientName, String date, String time);
    void sendPrescriptionUploadedEmail(String to, String patientName);
    void sendBillGeneratedEmail(String to, String patientName, String amount);
}
