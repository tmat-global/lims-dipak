package com.matglobal.lims.controller;

import com.matglobal.lims.entity.Billing;
import com.matglobal.lims.repository.BillingRepository;
import com.matglobal.lims.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingRepository billingRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String regNo,
            @RequestParam(required = false) String paymentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        LocalDateTime fromDt = from != null ? from.atStartOfDay() : LocalDate.now().atStartOfDay();
        LocalDateTime toDt = to != null ? to.atTime(23, 59, 59) : LocalDate.now().atTime(23, 59, 59);

        Page<Billing> billings = billingRepository.searchBillings(
            fromDt, toDt,
            (name != null && !name.isEmpty()) ? name : null,
            (mobile != null && !mobile.isEmpty()) ? mobile : null,
            (regNo != null && !regNo.isEmpty()) ? regNo : null,
            (paymentType != null && !paymentType.isEmpty()) ? paymentType : null,
            PageRequest.of(page, size, Sort.by("billDate").descending())
        );

        Page<Map<String, Object>> result = billings.map(b -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", b.getId());
            m.put("receiptNo", b.getReceiptNo());
            m.put("billNo", b.getBillNo());
            m.put("billDate", b.getBillDate());
            m.put("amtPaid", b.getAmtPaid());
            m.put("paymentType", b.getPaymentType());
            m.put("billAmt", b.getBillAmt());
            m.put("disAmt", b.getDisAmt());
            m.put("balAmt", b.getBalAmt());
            m.put("username", b.getUsername());
            // Eagerly access lazy fields within transaction
            if (b.getPatient() != null) {
                m.put("patientName", b.getPatient().getName());
                m.put("patientMobile", b.getPatient().getMobile());
                m.put("patientId", b.getPatient().getId());
            }
            if (b.getRegistration() != null) {
                m.put("regNo", b.getRegistration().getRegNo());
                m.put("registrationId", b.getRegistration().getId());
            }
            return m;
        });

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/daily-cash")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> dailyCash(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate d = date != null ? date : LocalDate.now();
        List<Billing> billings = billingRepository.findByDateRange(
            d.atStartOfDay(), d.atTime(23, 59, 59));

        Map<String, Double> byMode = new LinkedHashMap<>();
        byMode.put("Cash", 0.0);
        byMode.put("Card", 0.0);
        byMode.put("Online", 0.0);
        byMode.put("Cheque", 0.0);

        double totalPaid = 0, totalBill = 0, totalBalance = 0, totalDiscount = 0;

        for (Billing b : billings) {
            String mode = b.getPaymentType() != null ? b.getPaymentType() : "Cash";
            double paid = b.getAmtPaid() != null ? b.getAmtPaid() : 0;
            byMode.merge(mode, paid, Double::sum);
            totalPaid += paid;
            totalBill += b.getBillAmt() != null ? b.getBillAmt() : 0;
            totalBalance += b.getBalAmt() != null ? b.getBalAmt() : 0;
            totalDiscount += b.getDisAmt() != null ? b.getDisAmt() : 0;
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("date", d.toString());
        summary.put("totalBill", totalBill);
        summary.put("totalPaid", totalPaid);
        summary.put("totalBalance", totalBalance);
        summary.put("totalDiscount", totalDiscount);
        summary.put("byPaymentMode", byMode);
        summary.put("count", billings.size());

        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/cash-summary")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> cashSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        LocalDateTime fromDt = from != null ? from.atStartOfDay() : LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime toDt = to != null ? to.atTime(23, 59, 59) : LocalDate.now().atTime(23, 59, 59);

        List<Billing> billings = billingRepository.findByDateRange(fromDt, toDt);

        Map<String, Map<String, Object>> byOperator = new LinkedHashMap<>();
        for (Billing b : billings) {
            String user = b.getUsername() != null ? b.getUsername() : "Unknown";
            byOperator.computeIfAbsent(user, k -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("operator", k);
                m.put("totalBill", 0.0);
                m.put("totalPaid", 0.0);
                m.put("totalDiscount", 0.0);
                m.put("totalBalance", 0.0);
                m.put("count", 0);
                return m;
            });
            Map<String, Object> m = byOperator.get(user);
            m.put("totalBill", (Double)m.get("totalBill") + (b.getBillAmt() != null ? b.getBillAmt() : 0));
            m.put("totalPaid", (Double)m.get("totalPaid") + (b.getAmtPaid() != null ? b.getAmtPaid() : 0));
            m.put("totalDiscount", (Double)m.get("totalDiscount") + (b.getDisAmt() != null ? b.getDisAmt() : 0));
            m.put("totalBalance", (Double)m.get("totalBalance") + (b.getBalAmt() != null ? b.getBalAmt() : 0));
            m.put("count", (Integer)m.get("count") + 1);
        }

        return ResponseEntity.ok(ApiResponse.ok(new ArrayList<>(byOperator.values())));
    }

    @GetMapping("/registration/{registrationId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> byRegistration(
            @PathVariable Long registrationId) {
        List<Billing> billings = billingRepository.findByRegistrationId(registrationId);
        List<Map<String, Object>> result = billings.stream().map(b -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", b.getId());
            m.put("receiptNo", b.getReceiptNo());
            m.put("billNo", b.getBillNo());
            m.put("billDate", b.getBillDate());
            m.put("amtPaid", b.getAmtPaid());
            m.put("paymentType", b.getPaymentType());
            m.put("billAmt", b.getBillAmt());
            m.put("disAmt", b.getDisAmt());
            m.put("balAmt", b.getBalAmt());
            if (b.getPatient() != null) {
                m.put("patientName", b.getPatient().getName());
                m.put("patientMobile", b.getPatient().getMobile());
            }
            if (b.getRegistration() != null) {
                m.put("regNo", b.getRegistration().getRegNo());
            }
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
