package com.medbook.medicalrecord.service;

import com.medbook.medicalrecord.model.MedicalRecord;
import com.medbook.medicalrecord.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository repository;

    // Lấy tất cả hồ sơ
    public List<MedicalRecord> getAllRecords() {
        return repository.findAll();
    }

    // Lấy hồ sơ theo ID
    public Optional<MedicalRecord> getRecordById(Long id) {
        return repository.findById(id);
    }

    // Lấy danh sách hồ sơ theo bệnh nhân
    public List<MedicalRecord> getRecordsByPatientId(Long patientId) {
        return repository.findByPatientId(patientId);
    }

    // Tạo mới hồ sơ
    public MedicalRecord createRecord(MedicalRecord record) {
        return repository.save(record);
    }

    // Cập nhật hồ sơ
    public MedicalRecord updateRecord(Long id, MedicalRecord updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setDiagnosis(updated.getDiagnosis());
                    existing.setNotes(updated.getNotes());
                    existing.setDoctorId(updated.getDoctorId());
                    existing.setPatientId(updated.getPatientId());
                    existing.setRecordDate(updated.getRecordDate());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Medical record not found with ID: " + id));
    }

    // Xóa hồ sơ
    public void deleteRecord(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Medical record not found with ID: " + id);
        }
        repository.deleteById(id);
    }
}
