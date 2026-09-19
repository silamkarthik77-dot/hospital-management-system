package com.hms.controller;

import com.hms.dto.request.CreateInvoiceRequest;
import com.hms.dto.request.MakePaymentRequest;
import com.hms.dto.response.ApiResponse;
import com.hms.entity.Invoice;
import com.hms.entity.User;
import com.hms.service.BillingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Billing", description = "Invoices and payments")
public class BillingController {

    private final BillingService billingService;

    @PostMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ApiResponse<Invoice> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        return ApiResponse.ok("Invoice created", billingService.createInvoice(request));
    }

    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('PATIENT','RECEPTIONIST','ADMIN')")
    public ApiResponse<Invoice> pay(@Valid @RequestBody MakePaymentRequest request) {
        return ApiResponse.ok("Payment recorded", billingService.makePayment(request));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ApiResponse<List<Invoice>> myInvoices(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(billingService.getInvoicesForPatient(user.getId()));
    }

    @GetMapping("/invoices")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ApiResponse<List<Invoice>> allInvoices() {
        return ApiResponse.ok(billingService.getAllInvoices());
    }

    @GetMapping("/invoices/{id}/pdf")
    @PreAuthorize("hasAnyRole('PATIENT','ADMIN','RECEPTIONIST')")
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        byte[] pdf = billingService.generateInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
