package com.medbook.medicalrecord.repository;

import com.medbook.medicalrecord.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // nhớ import List

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Truy vấn tất cả hồ sơ theo patientId
    List<MedicalRecord> findByPatientId(Long patientId);
}
