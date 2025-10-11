package com.medbook.prescriptionservice.service;

import com.medbook.prescriptionservice.dto.PrescriptionRequest;
import com.medbook.prescriptionservice.dto.PrescriptionResponse;
import com.medbook.prescriptionservice.model.Prescription;
import com.medbook.prescriptionservice.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repo;

    // 🩺 Tạo mới toa thuốc
    @Transactional
    public PrescriptionResponse create(PrescriptionRequest req) {
        Prescription p = Prescription.builder()
                .medicalRecordId(req.medicalRecordId())
                .patientId(req.patientId())
                .doctorId(req.doctorId())
                .status(req.status())
                .notes(req.notes())
                .issuedAt(req.issuedAt() == null ? java.time.LocalDateTime.now() : req.issuedAt())
                .build();
        p = repo.save(p);
        return toDto(p);
    }

    // 🔍 Lấy toa thuốc theo ID
    public PrescriptionResponse get(Long id) {
        return repo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + id));
    }

    // 📋 Lấy danh sách toa thuốc theo ID hồ sơ bệnh án
    public List<PrescriptionResponse> byMedicalRecord(Long mrId) {
        return repo.findByMedicalRecordId(mrId).stream()
                .map(this::toDto)
                .toList();
    }

    // 🧍‍♀️ Lấy danh sách toa thuốc theo bệnh nhân
    public List<PrescriptionResponse> byPatient(Long patientId) {
        return repo.findByPatientId(patientId).stream()
                .map(this::toDto)
                .toList();
    }

    // 👨‍⚕️ Lấy danh sách toa thuốc theo bác sĩ
    public List<PrescriptionResponse> byDoctor(Long doctorId) {
        return repo.findByDoctorId(doctorId).stream()
                .map(this::toDto)
                .toList();
    }

    // ⚙️ Cập nhật trạng thái toa thuốc
    @Transactional
    public PrescriptionResponse updateStatus(Long id, String status) {
        Prescription p = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found: " + id));
        p.setStatus(status);
        return toDto(repo.save(p));
    }

    // ❌ Xóa toa thuốc
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // 🔄 Chuyển entity -> DTO
    private PrescriptionResponse toDto(Prescription p) {
        return new PrescriptionResponse(
                p.getId(),
                p.getMedicalRecordId(),
                p.getPatientId(),
                p.getDoctorId(),
                p.getStatus(),
                p.getNotes(),
                p.getIssuedAt()
        );
    }
}
