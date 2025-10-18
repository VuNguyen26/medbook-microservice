package com.medbook.medicalrecord.controller;

import com.medbook.medicalrecord.model.MedicalRecord;
import com.medbook.medicalrecord.service.MedicalRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalrecords")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    // Lấy tất cả hồ sơ
    @GetMapping
    public List<MedicalRecord> getAllRecords() {
        return service.getAllRecords();
    }

    // Lấy hồ sơ theo ID
    @GetMapping("/{id}")
    public MedicalRecord getRecordById(@PathVariable Long id) {
        return service.getRecordById(id).orElse(null);
    }

    // Lấy hồ sơ theo bệnh nhân
    @GetMapping("/patient/{patientId}")
    public List<MedicalRecord> getRecordsByPatientId(@PathVariable("patientId") Long patientId) {
        return service.getRecordsByPatientId(patientId);
    }

    // Tạo hồ sơ mới
    @PostMapping
    public MedicalRecord addRecord(@RequestBody MedicalRecord record) {
        return service.createRecord(record);
    }

    // Cập nhật hồ sơ
    @PutMapping("/{id}")
    public MedicalRecord updateRecord(@PathVariable Long id, @RequestBody MedicalRecord record) {
        return service.updateRecord(id, record);
    }

    // Xóa hồ sơ
    @DeleteMapping("/{id}")
    public void deleteRecord(@PathVariable Long id) {
        service.deleteRecord(id);
    }

    // Test endpoint
    @GetMapping("/test")
    public String test() {
        return "MedicalRecord service is working!";
    }
}
