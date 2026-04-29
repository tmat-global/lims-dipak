package com.matglobal.lims.controller;

import com.matglobal.lims.dto.response.ApiResponse;
import com.matglobal.lims.repository.BillingRepository;
import com.matglobal.lims.repository.RegistrationRepository;
import com.matglobal.lims.repository.ReferringDoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/account")
@RequiredArgsConstructor
public class AccountSectionController {

    private final BillingRepository billingRepository;
    private final RegistrationRepository registrationRepository;
    private final ReferringDoctorRepository referringDoctorRepository;

    // Center Sale / Income Report
    @GetMapping("/center-sale")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String,Object>>> centerSale(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String refDoc) {

        LocalDateTime fromDt = from != null ? from.atStartOfDay() : LocalDate.now().atStartOfDay();
        LocalDateTime toDt   = to   != null ? to.atTime(23,59,59) : LocalDate.now().atTime(23,59,59);

        var billings = billingRepository.findByDateRange(fromDt, toDt);

        // Filter by ref doc if provided
        if (refDoc != null && !refDoc.isEmpty()) {
            String q = refDoc.toLowerCase();
            billings = billings.stream().filter(b ->
                b.getRegistration() != null &&
                b.getRegistration().getRefDoctor() != null &&
                b.getRegistration().getRefDoctor().getName().toLowerCase().contains(q)
            ).collect(Collectors.toList());
        }

        // Center wise summary
        Map<String,Map<String,Object>> centerMap = new LinkedHashMap<>();
        double totalBill=0, totalPaid=0, totalDiscount=0, totalBalance=0;

        for (var b : billings) {
            String center = b.getRegistration() != null ? b.getRegistration().getCenter() : "Unknown";
            if (center == null) center = "Unknown";
            centerMap.computeIfAbsent(center, k -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("center",k); m.put("count",0); m.put("bill",0.0);
                m.put("paid",0.0); m.put("discount",0.0); m.put("balance",0.0);
                return m;
            });
            Map<String,Object> cm = centerMap.get(center);
            cm.put("count", (int)cm.get("count") + 1);
            double bill = b.getBillAmt()!=null?b.getBillAmt():0;
            double paid = b.getAmtPaid()!=null?b.getAmtPaid():0;
            double dis  = b.getDisAmt()!=null?b.getDisAmt():0;
            double bal  = b.getBalAmt()!=null?b.getBalAmt():0;
            cm.put("bill",     (double)cm.get("bill")     + bill);
            cm.put("paid",     (double)cm.get("paid")     + paid);
            cm.put("discount", (double)cm.get("discount") + dis);
            cm.put("balance",  (double)cm.get("balance")  + bal);
            totalBill+=bill; totalPaid+=paid; totalDiscount+=dis; totalBalance+=bal;
        }

        // Dr wise summary
        Map<String,Map<String,Object>> drMap = new LinkedHashMap<>();
        for (var b : billings) {
            String dr = "Self";
            if (b.getRegistration()!=null && b.getRegistration().getRefDoctor()!=null)
                dr = b.getRegistration().getRefDoctor().getName();
            drMap.computeIfAbsent(dr, k -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("doctor",k); m.put("count",0); m.put("bill",0.0); m.put("paid",0.0);
                return m;
            });
            Map<String,Object> dm = drMap.get(dr);
            dm.put("count", (int)dm.get("count") + 1);
            dm.put("bill", (double)dm.get("bill") + (b.getBillAmt()!=null?b.getBillAmt():0));
            dm.put("paid", (double)dm.get("paid") + (b.getAmtPaid()!=null?b.getAmtPaid():0));
        }

        Map<String,Object> result = new LinkedHashMap<>();
        result.put("from", fromDt.toLocalDate().toString());
        result.put("to",   toDt.toLocalDate().toString());
        result.put("totalBill",     totalBill);
        result.put("totalPaid",     totalPaid);
        result.put("totalDiscount", totalDiscount);
        result.put("totalBalance",  totalBalance);
        result.put("totalCount",    billings.size());
        result.put("centerWise",    new ArrayList<>(centerMap.values()));
        result.put("doctorWise",    new ArrayList<>(drMap.values()));
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    // Outstanding Report
    @GetMapping("/outstanding")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> outstanding(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDateTime fromDt = from!=null?from.atStartOfDay():LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime toDt   = to  !=null?to.atTime(23,59,59):LocalDate.now().atTime(23,59,59);
        var allRegs = registrationRepository.findAll();
        var regs = allRegs.stream()
            .filter(r -> r.getCreatedAt() != null &&
                !r.getCreatedAt().isBefore(fromDt) &&
                !r.getCreatedAt().isAfter(toDt))
            .collect(Collectors.toList());
        List<Map<String,Object>> rows = regs.stream()
            .filter(r -> r.getBalanceAmount()!=null && r.getBalanceAmount().doubleValue()>0)
            .map(r -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("regNo",    r.getRegNo());
                m.put("date",     r.getCreatedAt());
                m.put("name",     r.getPatient()!=null?r.getPatient().getName():"");
                m.put("mobile",   r.getPatient()!=null?r.getPatient().getMobile():"");
                m.put("center",   r.getCenter());
                m.put("charges",  r.getTotalAmount());
                m.put("paid",     r.getPaidAmount());
                m.put("balance",  r.getBalanceAmount());
                m.put("refDoctor",r.getRefDoctor()!=null?r.getRefDoctor().getName():"Self");
                return m;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(rows));
    }

    // Discount Report
    @GetMapping("/discount-report")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> discountReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDateTime fromDt = from!=null?from.atStartOfDay():LocalDate.now().atStartOfDay();
        LocalDateTime toDt   = to  !=null?to.atTime(23,59,59):LocalDate.now().atTime(23,59,59);
        var billings = billingRepository.findByDateRange(fromDt, toDt);
        List<Map<String,Object>> rows = billings.stream()
            .filter(b -> b.getDisAmt()!=null && b.getDisAmt()>0)
            .map(b -> {
                Map<String,Object> m = new LinkedHashMap<>();
                m.put("billNo",   b.getBillNo());
                m.put("billDate", b.getBillDate());
                m.put("name",     b.getPatient()!=null?b.getPatient().getName():"");
                m.put("regNo",    b.getRegistration()!=null?b.getRegistration().getRegNo():"");
                m.put("billAmt",  b.getBillAmt());
                m.put("discount", b.getDisAmt());
                m.put("paid",     b.getAmtPaid());
                m.put("balance",  b.getBalAmt());
                m.put("username", b.getUsername());
                return m;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(rows));
    }
}
