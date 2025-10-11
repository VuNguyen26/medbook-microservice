package com.medbook.medicalrecord.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MedicalRecordController {

    @GetMapping("/records/test")
    public String test() {
        return "✅ Medical Record Service is running!";
    }
}
