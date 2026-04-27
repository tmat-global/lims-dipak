package com.matglobal.lims.controller;

import com.matglobal.lims.dto.response.ApiResponse;
import com.matglobal.lims.entity.Center;
import com.matglobal.lims.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/centers")
@RequiredArgsConstructor
public class CenterController {

    private final CenterRepository centerRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Center>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(centerRepository.findByIsActiveTrue()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Center>> getById(@PathVariable Long id) {
        return centerRepository.findById(id)
                .map(c -> ResponseEntity.ok(ApiResponse.ok(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Center>> create(@RequestBody Center center) {
        if (centerRepository.existsByCode(center.getCode())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Center code already exists: " + center.getCode()));
        }
        center.setIsActive(true);
        Center saved = centerRepository.save(center);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Center created", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Center>> update(@PathVariable Long id, @RequestBody Center center) {
        return centerRepository.findById(id).map(existing -> {
            existing.setName(center.getName());
            existing.setMobile(center.getMobile());
            existing.setEmail(center.getEmail());
            existing.setContactName(center.getContactName());
            existing.setRateType(center.getRateType());
            existing.setAddress(center.getAddress());
            Center saved = centerRepository.save(existing);
            return ResponseEntity.ok(ApiResponse.ok("Center updated", saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return centerRepository.findById(id).map(c -> {
            c.setIsActive(false);
            centerRepository.save(c);
            return ResponseEntity.ok(ApiResponse.ok("Center deleted", null));
        }).orElse(ResponseEntity.notFound().build());
    }
}
