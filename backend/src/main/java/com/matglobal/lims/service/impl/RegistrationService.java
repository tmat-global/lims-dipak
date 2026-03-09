package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.RegistrationRequest;
import com.matglobal.lims.dto.response.RegistrationResponse;
import com.matglobal.lims.entity.*;
import com.matglobal.lims.exception.*;
import com.matglobal.lims.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final PatientRepository patientRepository;
    private final TestRepository testRepository;
    private final ReferringDoctorRepository refDoctorRepository;

    @Value("${app.reg.start-number:300001}")
    private long startNumber;

    private AtomicLong regSequence = null;

    private synchronized String nextRegNo() {
        if (regSequence == null) {
            long max = registrationRepository.count();
            regSequence = new AtomicLong(startNumber + max);
        }
        return String.valueOf(regSequence.getAndIncrement());
    }

    public RegistrationResponse create(RegistrationRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", req.getPatientId()));

        ReferringDoctor refDoc = req.getRefDoctorId() != null
                ? refDoctorRepository.findById(req.getRefDoctorId()).orElse(null)
                : null;

        // Calculate billing
        List<Test> tests = testRepository.findAllById(req.getTestIds());
        BigDecimal total = tests.stream().map(Test::getRate)
                .filter(r -> r != null).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal otherCharges = req.getOtherCharges() != null ? req.getOtherCharges() : BigDecimal.ZERO;
        BigDecimal discountAmt = BigDecimal.ZERO;
        if (req.getDiscountAmount() != null && req.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            if ("Per%".equals(req.getDiscountType())) {
                discountAmt = total.multiply(req.getDiscountAmount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                discountAmt = req.getDiscountAmount();
            }
        }
        BigDecimal netAmount = total.add(otherCharges).subtract(discountAmt);
        BigDecimal paidAmount = req.getPaidAmount() != null ? req.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balance = netAmount.subtract(paidAmount).max(BigDecimal.ZERO);

        Registration reg = Registration.builder()
                .regNo(nextRegNo()).patient(patient).refDoctor(refDoc)
                .patientType(req.getPatientType()).center(req.getCenter())
                .paymentType(req.getPaymentType())
                .totalAmount(total).otherCharges(otherCharges)
                .discountAmount(discountAmt).discountType(req.getDiscountType())
                .netAmount(netAmount).paidAmount(paidAmount).balanceAmount(balance)
                .remarks(req.getRemarks())
                .notifyOnLab(Boolean.TRUE.equals(req.getNotifyOnLab()))
                .notifyEmail(Boolean.TRUE.equals(req.getNotifyEmail()))
                .notifyWhatsapp(Boolean.TRUE.equals(req.getNotifyWhatsapp()))
                .isEmergency(Boolean.TRUE.equals(req.getIsEmergency()))
                .status(Registration.RegistrationStatus.REGISTERED)
                .build();

        // Add tests
        for (Test t : tests) {
            RegistrationTest rt = RegistrationTest.builder()
                    .registration(reg).test(t).rate(t.getRate())
                    .status(RegistrationTest.TestStatus.PENDING).build();
            reg.getRegistrationTests().add(rt);
        }

        return toResponse(registrationRepository.save(reg));
    }

    @Transactional(readOnly = true)
    public RegistrationResponse findById(Long id) {
        return toResponse(registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", id)));
    }

    @Transactional(readOnly = true)
    public Page<RegistrationResponse> search(LocalDateTime from, LocalDateTime to,
                                              String patientName, String mobile, String regNo,
                                              String status, int page, int size) {
        Registration.RegistrationStatus statusEnum = status != null && !status.isEmpty()
                ? Registration.RegistrationStatus.valueOf(status) : null;
        if (from == null) from = LocalDateTime.now().with(LocalTime.MIN);
        if (to == null) to = LocalDateTime.now().with(LocalTime.MAX);

        return registrationRepository.searchRegistrations(from, to, patientName, mobile, regNo, statusEnum,
                PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(this::toResponse);
    }

    public RegistrationResponse updateStatus(Long id, String newStatus) {
        Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", id));
        reg.setStatus(Registration.RegistrationStatus.valueOf(newStatus));
        return toResponse(registrationRepository.save(reg));
    }

    private RegistrationResponse toResponse(Registration r) {
        List<RegistrationResponse.TestInfo> testInfos = r.getRegistrationTests().stream()
                .map(rt -> RegistrationResponse.TestInfo.builder()
                        .id(rt.getTest().getId()).code(rt.getTest().getCode())
                        .name(rt.getTest().getName()).type(rt.getTest().getType())
                        .rate(rt.getRate()).clientRate(rt.getClientRate())
                        .status(rt.getStatus().name()).build())
                .collect(Collectors.toList());

        return RegistrationResponse.builder()
                .id(r.getId()).regNo(r.getRegNo())
                .patient(RegistrationResponse.PatientInfo.builder()
                        .id(r.getPatient().getId()).name(r.getPatient().getName())
                        .gender(r.getPatient().getGender()).age(r.getPatient().getAge())
                        .ageUnit(r.getPatient().getAgeUnit()).mobile(r.getPatient().getMobile())
                        .email(r.getPatient().getEmail()).build())
                .refDoctor(r.getRefDoctor() != null ? RegistrationResponse.RefDoctorInfo.builder()
                        .id(r.getRefDoctor().getId()).code(r.getRefDoctor().getCode())
                        .name(r.getRefDoctor().getName()).build() : null)
                .patientType(r.getPatientType()).center(r.getCenter())
                .paymentType(r.getPaymentType()).totalAmount(r.getTotalAmount())
                .otherCharges(r.getOtherCharges()).discountAmount(r.getDiscountAmount())
                .discountType(r.getDiscountType()).netAmount(r.getNetAmount())
                .paidAmount(r.getPaidAmount()).balanceAmount(r.getBalanceAmount())
                .remarks(r.getRemarks()).notifyOnLab(r.getNotifyOnLab())
                .notifyEmail(r.getNotifyEmail()).notifyWhatsapp(r.getNotifyWhatsapp())
                .isEmergency(r.getIsEmergency()).status(r.getStatus().name())
                .tests(testInfos).createdAt(r.getCreatedAt()).createdBy(r.getCreatedBy())
                .build();
    }
}
