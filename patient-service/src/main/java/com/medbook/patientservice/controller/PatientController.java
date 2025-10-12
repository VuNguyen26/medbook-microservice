package com.medbook.patientservice.controller;

import com.medbook.patientservice.model.Patient;
import com.medbook.patientservice.repository.PatientRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientRepository repository;

    public PatientController(PatientRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        return repository.findAll();
    }

    @PostMapping
    public Patient addPatient(@RequestBody Patient patient) {
        return repository.save(patient);
    }
}
