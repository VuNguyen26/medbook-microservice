package com.medbook.medicalrecord.repository;

import com.medbook.medicalrecord.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
}
