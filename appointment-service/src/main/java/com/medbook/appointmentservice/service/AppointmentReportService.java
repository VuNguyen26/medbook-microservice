package com.medbook.appointmentservice.service;

import com.medbook.appointmentservice.model.Appointment;
import com.medbook.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentReportService {

    private final AppointmentRepository appointmentRepository;

    public byte[] generateAppointmentsReport(LocalDate fromDate,
                                             LocalDate toDate,
                                             Integer doctorId) throws JRException {
        List<Appointment> all = appointmentRepository.findAll();

        // Lọc dữ liệu theo fromDate/toDate/doctorId giống use case thực tế
        List<Appointment> appointments = all.stream()
                .filter(a -> a.getAppointmentDate() != null)
                .filter(a -> fromDate == null || !a.getAppointmentDate().isBefore(fromDate))
                .filter(a -> toDate == null || !a.getAppointmentDate().isAfter(toDate))
                .filter(a -> doctorId == null || (a.getDoctorId() != null && a.getDoctorId().equals(doctorId)))
                .collect(Collectors.toList());

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(appointments);

        InputStream templateStream =
                this.getClass().getResourceAsStream("/reports/appointments-report.jrxml");
        if (templateStream == null) {
            throw new IllegalStateException("Không tìm thấy template Jasper: /reports/appointments-report.jrxml");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

        Map<String, Object> params = new HashMap<>();
        params.put("generatedAt", LocalDateTime.now());
        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        params.put("doctorId", doctorId);

        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);
        return JasperExportManager.exportReportToPdf(print);
    }
}


