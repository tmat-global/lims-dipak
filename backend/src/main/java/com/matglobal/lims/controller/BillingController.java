package com.matglobal.lims.controller;

import com.matglobal.lims.entity.Billing;
import com.matglobal.lims.entity.Registration;
import com.matglobal.lims.entity.RegistrationTest;
import com.matglobal.lims.repository.BillingRepository;
import com.matglobal.lims.repository.RegistrationRepository;
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
    private final RegistrationRepository registrationRepository;

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
            m.put("otherCharges", b.getOtherCharges());
            m.put("username", b.getUsername());
            if (b.getPatient() != null) {
                m.put("patientName", b.getPatient().getName());
                m.put("patientMobile", b.getPatient().getMobile());
                m.put("patientAge", b.getPatient().getAge());
                m.put("patientGender", b.getPatient().getGender());
                m.put("patientId", b.getPatient().getId());
            }
            if (b.getRegistration() != null) {
                m.put("regNo", b.getRegistration().getRegNo());
                m.put("registrationId", b.getRegistration().getId());
                m.put("center", b.getRegistration().getCenter());
                m.put("status", b.getRegistration().getStatus());
                m.put("refDoctor", b.getRegistration().getRefDoctor() != null ?
                    b.getRegistration().getRefDoctor().getName() : "SELF");
                // Tests list
                List<Map<String,Object>> tests = b.getRegistration().getRegistrationTests()
                    .stream().map(rt -> {
                        Map<String,Object> tm = new LinkedHashMap<>();
                        tm.put("testName", rt.getTest() != null ? rt.getTest().getName() : "");
                        tm.put("testCode", rt.getTest() != null ? rt.getTest().getCode() : "");
                        tm.put("charges", rt.getTest() != null ? rt.getTest().getRate() : 0);
                        tm.put("status", rt.getStatus());
                        tm.put("createdAt", rt.getCreatedAt());
                        return tm;
                    }).collect(Collectors.toList());
                m.put("tests", tests);
            }
            return m;
        });

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // ── NEW: Bill Desk using registrations ──
    @GetMapping("/bill-desk")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> billDesk(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String regNo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        LocalDateTime fromDt = from != null ? from.atStartOfDay() : LocalDate.now().atStartOfDay();
        LocalDateTime toDt   = to   != null ? to.atTime(23,59,59) : LocalDate.now().atTime(23,59,59);

        Page<Registration> regs = registrationRepository.searchRegistrations(
            fromDt, toDt,
            (name   != null && !name.isEmpty())   ? name   : null,
            (mobile != null && !mobile.isEmpty())  ? mobile : null,
            (regNo  != null && !regNo.isEmpty())   ? regNo  : null,
            null,
            PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        // Totals
        double totCharges = 0, totPaid = 0, totDiscount = 0, totBalance = 0;
        List<Map<String,Object>> rows = new ArrayList<>();

        for (Registration r : regs.getContent()) {
            double charges  = r.getTotalAmount()   != null ? r.getTotalAmount().doubleValue()   : 0;
            double paid     = r.getPaidAmount()     != null ? r.getPaidAmount().doubleValue()     : 0;
            double discount = r.getDiscountAmount() != null ? r.getDiscountAmount().doubleValue() : 0;
            double balance  = r.getBalanceAmount()  != null ? r.getBalanceAmount().doubleValue()  : 0;
            totCharges  += charges;
            totPaid     += paid;
            totDiscount += discount;
            totBalance  += balance;

            Map<String,Object> m = new LinkedHashMap<>();
            m.put("registrationId", r.getId());
            m.put("regNo",   r.getRegNo());
            m.put("date",    r.getCreatedAt());
            m.put("status",  r.getStatus());
            m.put("center",  r.getCenter());
            m.put("patientType", r.getPatientType());
            m.put("refDoctor", r.getRefDoctor() != null ? r.getRefDoctor().getName() : "SELF");
            m.put("charges",  charges);
            m.put("paid",     paid);
            m.put("discount", discount);
            m.put("balance",  balance);
            m.put("paymentType", r.getPaymentType());
            if (r.getPatient() != null) {
                m.put("patientId",     r.getPatient().getId());
                m.put("patientName",   r.getPatient().getName());
                m.put("patientMobile", r.getPatient().getMobile());
                m.put("patientAge",    r.getPatient().getAge());
                m.put("patientGender", r.getPatient().getGender());
                m.put("patientAddress",r.getPatient().getAddress());
            }
            List<String> testNames = r.getRegistrationTests().stream()
                .map(rt -> rt.getTest() != null ? rt.getTest().getName() : "")
                .filter(s -> !s.isEmpty()).collect(Collectors.toList());
            m.put("tests", String.join(", ", testNames));
            rows.add(m);
        }

        Map<String,Object> response = new LinkedHashMap<>();
        response.put("patients",     rows);
        response.put("totalCount",   regs.getTotalElements());
        response.put("totalCharges", totCharges);
        response.put("totalPaid",    totPaid);
        response.put("totalDiscount",totDiscount);
        response.put("totalBalance", totBalance);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    // ── NEW: Receipt data for a registration ──
    @GetMapping("/receipt/{registrationId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> receipt(
            @PathVariable Long registrationId) {

        Registration r = registrationRepository.findById(registrationId)
            .orElse(null);
        if (r == null) return ResponseEntity.notFound().build();

        // Get latest billing record
        List<Billing> billings = billingRepository.findByRegistrationId(registrationId);
        Billing b = billings.isEmpty() ? null : billings.get(billings.size() - 1);

        Map<String,Object> m = new LinkedHashMap<>();
        m.put("registrationId", r.getId());
        m.put("regNo",   r.getRegNo());
        m.put("center",  r.getCenter());
        m.put("billDate", r.getCreatedAt());
        m.put("refDoctor", r.getRefDoctor() != null ? r.getRefDoctor().getName() : "SELF");
        m.put("paymentType", r.getPaymentType());
        m.put("totalCharges", r.getTotalAmount());
        m.put("otherCharges", r.getOtherCharges());
        m.put("discount",  r.getDiscountAmount());
        m.put("netAmount", r.getNetAmount());
        m.put("amtPaid",   r.getPaidAmount());
        m.put("balance",   r.getBalanceAmount());

        if (b != null) {
            m.put("billNo",    b.getBillNo());
            m.put("receiptNo", b.getReceiptNo());
            m.put("billDate",  b.getBillDate() != null ? b.getBillDate() : r.getCreatedAt());
        } else {
            m.put("billNo",    r.getId());
            m.put("receiptNo", r.getId());
        }

        if (r.getPatient() != null) {
            m.put("patientId",     r.getPatient().getId());
            m.put("patientName",   r.getPatient().getName());
            m.put("patientMobile", r.getPatient().getMobile());
            m.put("patientAge",    r.getPatient().getAge());
            m.put("patientGender", r.getPatient().getGender());
            m.put("patientAddress",r.getPatient().getAddress());
        }

        List<Map<String,Object>> tests = r.getRegistrationTests().stream().map(rt -> {
            Map<String,Object> tm = new LinkedHashMap<>();
            tm.put("testName",  rt.getTest() != null ? rt.getTest().getName() : "");
            tm.put("testCode",  rt.getTest() != null ? rt.getTest().getCode() : "");
            tm.put("rate",      rt.getTest() != null ? rt.getTest().getRate() : 0);
            tm.put("qty",       1);
            tm.put("createdAt", rt.getCreatedAt());
            return tm;
        }).collect(Collectors.toList());
        m.put("tests", tests);

        return ResponseEntity.ok(ApiResponse.ok(m));
    }

    @GetMapping("/daily-cash")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> dailyCash(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate d = date != null ? date : LocalDate.now();
        List<Billing> billings = billingRepository.findByDateRange(d.atStartOfDay(), d.atTime(23,59,59));
        Map<String,Double> byMode = new LinkedHashMap<>();
        byMode.put("Cash",0.0); byMode.put("Card",0.0); byMode.put("Online",0.0); byMode.put("Cheque",0.0);
        double totalPaid=0, totalBill=0, totalBalance=0, totalDiscount=0;
        for (Billing b : billings) {
            String mode = b.getPaymentType() != null ? b.getPaymentType() : "Cash";
            double paid = b.getAmtPaid() != null ? b.getAmtPaid() : 0;
            byMode.merge(mode, paid, Double::sum);
            totalPaid    += paid;
            totalBill    += b.getBillAmt()  != null ? b.getBillAmt()  : 0;
            totalBalance += b.getBalAmt()   != null ? b.getBalAmt()   : 0;
            totalDiscount+= b.getDisAmt()   != null ? b.getDisAmt()   : 0;
        }
        Map<String,Object> summary = new LinkedHashMap<>();
        summary.put("date",d.toString()); summary.put("totalBill",totalBill);
        summary.put("totalPaid",totalPaid); summary.put("totalBalance",totalBalance);
        summary.put("totalDiscount",totalDiscount); summary.put("byPaymentMode",byMode);
        summary.put("count",billings.size());
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/cash-summary")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> cashSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDateTime fromDt = from != null ? from.atStartOfDay() : LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime toDt   = to   != null ? to.atTime(23,59,59) : LocalDate.now().atTime(23,59,59);
        List<Billing> billings = billingRepository.findByDateRange(fromDt, toDt);
        Map<String,Map<String,Object>> byOp = new LinkedHashMap<>();
        for (Billing b : billings) {
            String user = b.getUsername() != null ? b.getUsername() : "Unknown";
            byOp.computeIfAbsent(user, k -> { Map<String,Object> mm = new LinkedHashMap<>();
                mm.put("operator",k); mm.put("totalBill",0.0); mm.put("totalPaid",0.0);
                mm.put("totalDiscount",0.0); mm.put("totalBalance",0.0); mm.put("count",0); return mm; });
            Map<String,Object> mm = byOp.get(user);
            mm.put("totalBill",    (Double)mm.get("totalBill")     + (b.getBillAmt()  !=null?b.getBillAmt() :0));
            mm.put("totalPaid",    (Double)mm.get("totalPaid")     + (b.getAmtPaid()  !=null?b.getAmtPaid() :0));
            mm.put("totalDiscount",(Double)mm.get("totalDiscount") + (b.getDisAmt()   !=null?b.getDisAmt()  :0));
            mm.put("totalBalance", (Double)mm.get("totalBalance")  + (b.getBalAmt()   !=null?b.getBalAmt()  :0));
            mm.put("count", (Integer)mm.get("count") + 1);
        }
        return ResponseEntity.ok(ApiResponse.ok(new ArrayList<>(byOp.values())));
    }

    @GetMapping("/registration/{registrationId}/billings")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> byRegistration(@PathVariable Long registrationId) {
        List<Billing> billings = billingRepository.findByRegistrationId(registrationId);
        List<Map<String,Object>> result = billings.stream().map(b -> {
            Map<String,Object> m = new LinkedHashMap<>();
            m.put("id",b.getId()); m.put("receiptNo",b.getReceiptNo()); m.put("billNo",b.getBillNo());
            m.put("billDate",b.getBillDate()); m.put("amtPaid",b.getAmtPaid());
            m.put("paymentType",b.getPaymentType()); m.put("billAmt",b.getBillAmt());
            m.put("disAmt",b.getDisAmt()); m.put("balAmt",b.getBalAmt());
            if (b.getPatient()!=null) { m.put("patientName",b.getPatient().getName()); m.put("patientMobile",b.getPatient().getMobile()); }
            if (b.getRegistration()!=null) m.put("regNo",b.getRegistration().getRegNo());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
