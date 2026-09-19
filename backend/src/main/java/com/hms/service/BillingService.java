package com.hms.service;

import com.hms.dto.request.CreateInvoiceRequest;
import com.hms.dto.request.MakePaymentRequest;
import com.hms.entity.Invoice;

import java.util.List;

public interface BillingService {
    Invoice createInvoice(CreateInvoiceRequest request);
    Invoice makePayment(MakePaymentRequest request);
    List<Invoice> getInvoicesForPatient(Long patientUserId);
    List<Invoice> getAllInvoices();
    byte[] generateInvoicePdf(Long invoiceId);
}
