package com.medbook.notificationservice.service;

import com.medbook.notificationservice.dto.BookingSuccessEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username:}")
    private String from;

    public void sendBookingSuccessEmail(BookingSuccessEmailRequest dto) throws MessagingException {
        // Fallback demo: nếu backend chưa cung cấp tên thì dùng giá trị mặc định
        String patientName = (dto.getPatientName() != null && !dto.getPatientName().isBlank())
                ? dto.getPatientName()
                : "Phan Chí Bảo";
        String doctorName = (dto.getDoctorName() != null && !dto.getDoctorName().isBlank())
                ? dto.getDoctorName()
                : "Nguyễn Trương Khương";

        Context ctx = new Context();
        ctx.setVariable("patientName", patientName);
        ctx.setVariable("doctorName", doctorName);
        ctx.setVariable("appointmentDate", dto.getAppointmentDate());
        ctx.setVariable("appointmentTime", dto.getAppointmentTime());

        String html = templateEngine.process("payment-booking-success", ctx);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
        helper.setTo(dto.getEmail());
        if (from != null && !from.isBlank()) {
            helper.setFrom(from);
        }
        helper.setSubject(String.format(
                "Thông báo khách hàng \"%s\" đã đặt lịch thành công",
                patientName
        ));
        helper.setText(html, true);

        mailSender.send(message);
    }
}


