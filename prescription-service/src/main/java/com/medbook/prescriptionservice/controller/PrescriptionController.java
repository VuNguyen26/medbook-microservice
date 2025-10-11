package com.medbook.prescriptionservice.controller;

import com.medbook.prescriptionservice.dto.PrescriptionRequest;
import com.medbook.prescriptionservice.dto.PrescriptionResponse;
import com.medbook.prescriptionservice.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService service;

    // 🩺 Tạo mới toa thuốc
    @PostMapping
    public ResponseEntity<PrescriptionResponse> create(@Valid @RequestBody PrescriptionRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // 🔍 Lấy toa thuốc theo ID
    @GetMapping("/{id}")
    public PrescriptionResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // 🔎 Lọc toa thuốc theo medicalRecordId / patientId / doctorId
    @GetMapping
    public List<PrescriptionResponse> findPrescriptions(
            @RequestParam(required = false) Long medicalRecordId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId) {

        if (medicalRecordId != null) {
            return service.byMedicalRecord(medicalRecordId);
        }
        if (patientId != null) {
            return service.byPatient(patientId);
        }
        if (doctorId != null) {
            return service.byDoctor(doctorId);
        }
        return List.of();
    }

    // ⚙️ Cập nhật trạng thái toa thuốc (ACTIVE / CANCELLED / ...)
    @PatchMapping("/{id}/status")
    public PrescriptionResponse updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return service.updateStatus(id, body.getOrDefault("status", "ACTIVE"));
    }

    // ❌ Xóa toa thuốc
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
