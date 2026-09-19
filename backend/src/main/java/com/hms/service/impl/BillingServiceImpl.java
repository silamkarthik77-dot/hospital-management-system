package com.hms.service.impl;

import com.hms.dto.request.CreateInvoiceRequest;
import com.hms.dto.request.MakePaymentRequest;
import com.hms.entity.*;
import com.hms.exception.BadRequestException;
import com.hms.exception.ResourceNotFoundException;
import com.hms.repository.*;
import com.hms.service.BillingService;
import com.hms.service.EmailService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Invoice createInvoice(CreateInvoiceRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Appointment appointment = null;

        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        }

        BigDecimal tax = request.getTaxAmount() != null
                ? request.getTaxAmount()
                : BigDecimal.ZERO;

        BigDecimal total = request.getAmount().add(tax);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(
                        "INV-" + UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase()
                )
                .patient(patient)
                .appointment(appointment)
                .billType(request.getBillType())
                .amount(request.getAmount())
                .taxAmount(tax)
                .totalAmount(total)
                .status(PaymentStatus.PENDING)
                .build();

        invoice = invoiceRepository.save(invoice);

        emailService.sendBillGeneratedEmail(
                patient.getUser().getEmail(),
                patient.getUser().getFullName(),
                total.toString()
        );

        return invoice;
    }

    @Override
    @Transactional
    public Invoice makePayment(MakePaymentRequest request) {

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

        if (invoice.getStatus() == PaymentStatus.PAID) {
            throw new BadRequestException(
                    "This invoice has already been paid in full"
            );
        }

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amountPaid(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(UUID.randomUUID().toString())
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        BigDecimal totalPaid = paymentRepository
                .findByInvoice_Id(invoice.getId())
                .stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setStatus(
                totalPaid.compareTo(invoice.getTotalAmount()) >= 0
                        ? PaymentStatus.PAID
                        : PaymentStatus.PARTIALLY_PAID
        );

        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public List<Invoice> getInvoicesForPatient(Long patientUserId) {

        Patient patient = patientRepository.findByUser_Id(patientUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient profile not found")
                );

        return invoiceRepository.findByPatient_Id(patient.getId());
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public byte[] generateInvoicePdf(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found")
                );

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(
                    new Paragraph("Hospital Management System")
                            .setBold()
                            .setFontSize(18)
            );

            document.add(
                    new Paragraph("Invoice: " + invoice.getInvoiceNumber())
            );

            document.add(
                    new Paragraph(
                            "Patient: "
                                    + invoice.getPatient()
                                    .getUser()
                                    .getFullName()
                    )
            );

            document.add(
                    new Paragraph("Status: " + invoice.getStatus())
            );

            Table table = new Table(2);

            table.addCell("Bill Type");
            table.addCell(invoice.getBillType().name());

            table.addCell("Amount");
            table.addCell(invoice.getAmount().toString());

            table.addCell("Tax");
            table.addCell(invoice.getTaxAmount().toString());

            table.addCell("Total");
            table.addCell(invoice.getTotalAmount().toString());

            document.add(table);

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to generate invoice PDF",
                    e
            );
        }
    }
}
