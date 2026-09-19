package com.hms.service.impl;

import com.hms.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Sends transactional emails asynchronously so request threads are never
 * blocked on SMTP latency. Failures are logged, not propagated - a
 * notification hiccup should never fail the underlying business action.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Failed to send email to {}: {}", to, ex.getMessage());
        }
    }

    @Override
    public void sendRegistrationEmail(String to, String fullName) {
        sendSimpleEmail(to, "Welcome to Hospital Management System",
                "Hi " + fullName + ",\n\nYour account has been created successfully. You can now log in and book appointments.\n\nRegards,\nHMS Team");
    }

    @Override
    public void sendAppointmentConfirmationEmail(String to, String patientName, String doctorName, String date, String time) {
        sendSimpleEmail(to, "Appointment Confirmed",
                "Hi " + patientName + ",\n\nYour appointment with Dr. " + doctorName + " on " + date + " at " + time + " has been confirmed.\n\nRegards,\nHMS Team");
    }

    @Override
    public void sendAppointmentCancellationEmail(String to, String patientName, String date, String time) {
        sendSimpleEmail(to, "Appointment Cancelled",
                "Hi " + patientName + ",\n\nYour appointment on " + date + " at " + time + " has been cancelled.\n\nRegards,\nHMS Team");
    }

    @Override
    public void sendPrescriptionUploadedEmail(String to, String patientName) {
        sendSimpleEmail(to, "New Prescription Available",
                "Hi " + patientName + ",\n\nA new prescription has been uploaded to your account. Please log in to view it.\n\nRegards,\nHMS Team");
    }

    @Override
    public void sendBillGeneratedEmail(String to, String patientName, String amount) {
        sendSimpleEmail(to, "New Bill Generated",
                "Hi " + patientName + ",\n\nA new bill of amount " + amount + " has been generated on your account. Please log in to view and pay.\n\nRegards,\nHMS Team");
    }
}
