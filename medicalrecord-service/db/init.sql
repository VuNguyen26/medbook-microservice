CREATE DATABASE IF NOT EXISTS medbook_medicalrecorddb;
USE medbook_medicalrecorddb;

CREATE TABLE `medical_records` (
  `id` bigint(20) NOT NULL,
  `diagnosis` varchar(255) DEFAULT NULL,
  `doctor_id` bigint(20) DEFAULT NULL,
  `notes` varchar(1000) DEFAULT NULL,
  `patient_id` bigint(20) DEFAULT NULL,
  `record_date` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `medical_records` (`id`, `diagnosis`, `doctor_id`, `notes`, `patient_id`, `record_date`, `updated_at`) VALUES
(1, 'Đau đầu mãn tính', NULL, 'Bệnh nhân bị đau đầu thường xuyên do căng thẳng. Khuyên nghỉ ngơi, bổ sung vitamin nhóm B và giảm làm việc máy tính.', NULL, '2025-10-08 09:30:00.000000', '2025-10-06 22:51:48.000000'),
(2, 'Thoái hóa đốt sống cổ', NULL, 'Bệnh nhân có triệu chứng mỏi vai gáy. Đề nghị vật lý trị liệu kết hợp thuốc giãn cơ.', NULL, '2025-10-09 10:45:00.000000', '2025-10-06 22:51:48.000000'),
(3, 'Đau khớp gối nhẹ', NULL, 'Khuyến khích tập thể dục nhẹ, uống thuốc giảm đau khi cần. Hẹn tái khám sau 2 tuần.', NULL, '2025-10-09 14:30:00.000000', '2025-10-06 22:51:48.000000'),
(4, 'Viêm cổ tử cung', NULL, 'Điều trị bằng kháng sinh 7 ngày. Tránh quan hệ trong thời gian điều trị.', NULL, '2025-10-10 15:50:00.000000', '2025-10-06 22:51:48.000000'),
(5, 'Viêm da dị ứng', NULL, 'Cho dùng thuốc bôi chứa corticoid nhẹ và thuốc kháng histamin đường uống.', NULL, '2025-10-11 10:00:00.000000', '2025-10-06 22:51:48.000000'),
(6, 'Sốt siêu vi', NULL, 'Khuyến nghị theo dõi nhiệt độ và bù nước điện giải. Hẹn tái khám nếu sốt trên 3 ngày.', NULL, '2025-10-12 09:00:00.000000', '2025-10-06 22:51:48.000000'),
(7, 'Viêm dạ dày nhẹ', NULL, 'Khuyên giảm đồ cay, tránh ăn khuya. Dùng thuốc giảm acid dạ dày.', NULL, '2025-10-13 10:30:00.000000', '2025-10-06 22:51:48.000000'),
(8, 'Rối loạn giấc ngủ', NULL, 'Đề xuất thay đổi thói quen sinh hoạt, hạn chế caffeine và tập thiền.', NULL, '2025-10-14 13:50:00.000000', '2025-10-06 22:51:48.000000'),
(9, 'Gan nhiễm mỡ độ 1', NULL, 'Khuyên tập thể dục và giảm ăn mỡ động vật, tái khám định kỳ 3 tháng.', NULL, '2025-10-15 11:30:00.000000', '2025-10-06 22:51:48.000000');

ALTER TABLE `medical_records`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
