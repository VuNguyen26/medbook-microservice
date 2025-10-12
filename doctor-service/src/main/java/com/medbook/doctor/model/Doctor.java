package com.medbook.doctor.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String title;         // học vị, ví dụ: ThS., BS., TS.
    private String email;
    private String phone;
    private String specialty;     // chuyên khoa (vd: Tim mạch, Da liễu,...)
    private Double fee;           // giá khám
    private Double rating;        // điểm đánh giá trung bình
    private Integer experience;   // số năm kinh nghiệm

    @Column(name = "image_url")
    private String imageUrl;      // ảnh đại diện bác sĩ (link)
}
