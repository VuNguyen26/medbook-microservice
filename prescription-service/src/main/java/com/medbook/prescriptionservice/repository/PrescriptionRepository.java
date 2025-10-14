package com.medbook.prescriptionservice.repository;

import com.medbook.prescriptionservice.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByMedicalRecordId(Long medicalRecordId);

    List<Prescription> findByPatientId(Long patientId);

    List<Prescription> findByDoctorId(Long doctorId);
}
