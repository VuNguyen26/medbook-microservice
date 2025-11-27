package com.medbook.patientservice.controller;

import com.medbook.patientservice.model.Patient;
import com.medbook.patientservice.repository.PatientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientRepository repository;

    public PatientController(PatientRepository repository) {
        this.repository = repository;
    }

    // 🔹 GET all patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = repository.findAll();
        return ResponseEntity.ok(patients);
    }

    // 🔹 GET patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Optional<Patient> patient = repository.findById(id);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 GET patient by EMAIL (⭐ API cần cho AppointmentService)
    @GetMapping("/email/{email}")
    public ResponseEntity<Patient> getPatientByEmail(@PathVariable String email) {
        return repository.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 CREATE new patient
    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient) {
        Patient saved = repository.save(patient);
        return ResponseEntity.ok(saved);
    }

    // 🔹 UPDATE patient
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient updatedPatient) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setFullName(updatedPatient.getFullName());
                    existing.setEmail(updatedPatient.getEmail());
                    existing.setPhone(updatedPatient.getPhone());
                    existing.setAddress(updatedPatient.getAddress());
                    existing.setGender(updatedPatient.getGender());
                    existing.setAge(updatedPatient.getAge());
                    Patient saved = repository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 DELETE patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
