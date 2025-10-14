package com.medbook.prescriptionservice.controller;

import com.medbook.prescriptionservice.dto.PrescriptionRequest;
import com.medbook.prescriptionservice.dto.PrescriptionResponse;
import com.medbook.prescriptionservice.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        PrescriptionResponse created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 🔍 Lấy toa thuốc theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    // 🔎 Lọc toa thuốc theo medicalRecordId / patientId / doctorId / hoặc trả tất cả
    @GetMapping
    public ResponseEntity<List<PrescriptionResponse>> findPrescriptions(
            @RequestParam(required = false) Long medicalRecordId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId) {

        List<PrescriptionResponse> result;

        if (medicalRecordId != null) {
            result = service.byMedicalRecord(medicalRecordId);
        } else if (patientId != null) {
            result = service.byPatient(patientId);
        } else if (doctorId != null) {
            result = service.byDoctor(doctorId);
        } else {
            // Nếu không có filter -> trả toàn bộ danh sách
            result = service.getAll();
        }

        return ResponseEntity.ok(result);
    }

    // ⚙️ Cập nhật trạng thái toa thuốc (ACTIVE / CANCELLED / ...)
    @PatchMapping("/{id}/status")
    public ResponseEntity<PrescriptionResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        PrescriptionResponse updated = service.updateStatus(id, body.getOrDefault("status", "ACTIVE"));
        return ResponseEntity.ok(updated);
    }

    // ❌ Xóa toa thuốc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}
