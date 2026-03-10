package com.matglobal.lims.service.impl;

import com.matglobal.lims.dto.request.RegistrationRequest;
import com.matglobal.lims.dto.response.RegistrationResponse;
import com.matglobal.lims.entity.*;
import com.matglobal.lims.exception.ResourceNotFoundException;
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
import java.util.ArrayList;
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
            regSequence = new AtomicLong(startNumber + registrationRepository.count());
        }
        return String.valueOf(regSequence.getAndIncrement());
    }

    public RegistrationResponse create(RegistrationRequest req) {
        Patient patient = patientRepository.findById(req.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", req.getPatientId()));

        ReferringDoctor refDoc = req.getRefDoctorId() != null
                ? refDoctorRepository.findById(req.getRefDoctorId()).orElse(null) : null;

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

        Registration reg = new Registration();
        reg.setRegNo(nextRegNo());
        reg.setPatient(patient);
        reg.setRefDoctor(refDoc);
        reg.setPatientType(req.getPatientType());
        reg.setCenter(req.getCenter());
        reg.setPaymentType(req.getPaymentType());
        reg.setTotalAmount(total);
        reg.setOtherCharges(otherCharges);
        reg.setDiscountAmount(discountAmt);
        reg.setDiscountType(req.getDiscountType());
        reg.setNetAmount(netAmount);
        reg.setPaidAmount(paidAmount);
        reg.setBalanceAmount(balance);
        reg.setRemarks(req.getRemarks());
        reg.setNotifyOnLab(Boolean.TRUE.equals(req.getNotifyOnLab()));
        reg.setNotifyEmail(Boolean.TRUE.equals(req.getNotifyEmail()));
        reg.setNotifyWhatsapp(Boolean.TRUE.equals(req.getNotifyWhatsapp()));
        reg.setIsEmergency(Boolean.TRUE.equals(req.getIsEmergency()));
        reg.setStatus(Registration.RegistrationStatus.REGISTERED);

        for (Test t : tests) {
            RegistrationTest rt = new RegistrationTest();
            rt.setRegistration(reg);
            rt.setTest(t);
            rt.setRate(t.getRate());
            rt.setStatus(RegistrationTest.TestStatus.PENDING);
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
            String patientName, String mobile, String regNo, String status, int page, int size) {
        Registration.RegistrationStatus statusEnum = (status != null && !status.isEmpty())
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
        List<RegistrationResponse.TestInfo> testInfos = r.getRegistrationTests().stream().map(rt -> {
            RegistrationResponse.TestInfo ti = new RegistrationResponse.TestInfo();
            ti.setId(rt.getTest().getId());
            ti.setCode(rt.getTest().getCode());
            ti.setName(rt.getTest().getName());
            ti.setType(rt.getTest().getType());
            ti.setRate(rt.getRate());
            ti.setClientRate(rt.getClientRate());
            ti.setStatus(rt.getStatus().name());
            return ti;
        }).collect(Collectors.toList());

        RegistrationResponse.PatientInfo pi = new RegistrationResponse.PatientInfo();
        pi.setId(r.getPatient().getId());
        pi.setName(r.getPatient().getName());
        pi.setGender(r.getPatient().getGender());
        pi.setAge(r.getPatient().getAge());
        pi.setAgeUnit(r.getPatient().getAgeUnit());
        pi.setMobile(r.getPatient().getMobile());
        pi.setEmail(r.getPatient().getEmail());

        RegistrationResponse.RefDoctorInfo rdi = null;
        if (r.getRefDoctor() != null) {
            rdi = new RegistrationResponse.RefDoctorInfo();
            rdi.setId(r.getRefDoctor().getId());
            rdi.setCode(r.getRefDoctor().getCode());
            rdi.setName(r.getRefDoctor().getName());
        }

        RegistrationResponse res = new RegistrationResponse();
        res.setId(r.getId());
        res.setRegNo(r.getRegNo());
        res.setPatient(pi);
        res.setRefDoctor(rdi);
        res.setPatientType(r.getPatientType());
        res.setCenter(r.getCenter());
        res.setPaymentType(r.getPaymentType());
        res.setTotalAmount(r.getTotalAmount());
        res.setOtherCharges(r.getOtherCharges());
        res.setDiscountAmount(r.getDiscountAmount());
        res.setDiscountType(r.getDiscountType());
        res.setNetAmount(r.getNetAmount());
        res.setPaidAmount(r.getPaidAmount());
        res.setBalanceAmount(r.getBalanceAmount());
        res.setRemarks(r.getRemarks());
        res.setNotifyOnLab(r.getNotifyOnLab());
        res.setNotifyEmail(r.getNotifyEmail());
        res.setNotifyWhatsapp(r.getNotifyWhatsapp());
        res.setIsEmergency(r.getIsEmergency());
        res.setStatus(r.getStatus().name());
        res.setTests(testInfos);
        res.setCreatedAt(r.getCreatedAt());
        res.setCreatedBy(r.getCreatedBy());
        return res;
    }
}
