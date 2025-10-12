package com.medbook.medicalrecord.controller;

import com.medbook.medicalrecord.model.MedicalRecord;
import com.medbook.medicalrecord.repository.MedicalRecordRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalrecords")
public class MedicalRecordController {

    private final MedicalRecordRepository repository;

    public MedicalRecordController(MedicalRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<MedicalRecord> getAllRecords() {
        return repository.findAll();
    }

    @PostMapping
    public MedicalRecord addRecord(@RequestBody MedicalRecord record) {
        return repository.save(record);
    }
}
