package com.medbook.appointmentservice.dto;

import lombok.Data;

@Data
public class RatingRequest {
    private Integer rating;      // 1–5
    private String comment;      // nội dung đánh giá
}
