CREATE DATABASE IF NOT EXISTS medbook_appointmentdb;
USE medbook_appointmentdb;

CREATE TABLE `appointments` (
  `id` bigint(20) NOT NULL,
  `appointment_date` datetime(6) DEFAULT NULL,
  `appointment_time` time(6) DEFAULT NULL,
  `appointment_type` varchar(255) DEFAULT NULL,
  `clinic_location_id` bigint(20) DEFAULT NULL,
  `doctor_id` int(11) DEFAULT NULL,
  `fee` double DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `paid` bit(1) DEFAULT NULL,
  `patient_id` int(11) DEFAULT NULL,
  `payment_status` varchar(255) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `service_id` int(11) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `appointments` (`id`, `appointment_date`, `appointment_time`, `appointment_type`, `clinic_location_id`, `doctor_id`, `fee`, `notes`, `paid`, `patient_id`, `payment_status`, `reason`, `service_id`, `status`) VALUES
(1, '2025-10-08 00:00:00.000000', '09:00:00.000000', NULL, NULL, 1, NULL, 'Đau đầu kéo dài 3 ngày', NULL, 1, 'paid', NULL, 1, 'confirmed'),
(2, '2025-10-09 00:00:00.000000', '10:30:00.000000', NULL, NULL, 2, NULL, 'Mất ngủ và tê tay', NULL, 2, 'unpaid', NULL, 1, 'pending'),
(3, '2025-10-09 00:00:00.000000', '14:00:00.000000', NULL, NULL, 3, NULL, 'Kiểm tra sức khỏe định kỳ', NULL, 3, 'paid', NULL, 2, 'completed'),
(4, '2025-10-10 00:00:00.000000', '15:30:00.000000', NULL, NULL, 5, NULL, 'Đầy bụng, khó tiêu sau bữa ăn', NULL, 4, 'paid', NULL, 6, 'confirmed'),
(5, '2025-10-11 00:00:00.000000', '09:45:00.000000', NULL, NULL, 6, NULL, 'Nổi mẩn đỏ, nghi dị ứng mỹ phẩm', NULL, 5, 'unpaid', NULL, 3, 'pending'),
(6, '2025-10-12 00:00:00.000000', '08:30:00.000000', NULL, NULL, 7, NULL, 'Đưa con đi khám sốt', NULL, 2, 'paid', NULL, 4, 'completed'),
(7, '2025-10-13 00:00:00.000000', '10:00:00.000000', NULL, NULL, 8, NULL, 'Khám sức khỏe định kỳ', NULL, 4, 'paid', NULL, 2, 'confirmed'),
(8, '2025-10-14 00:00:00.000000', '13:30:00.000000', NULL, NULL, 9, NULL, 'Khám sức khỏe phụ khoa', NULL, 5, 'unpaid', NULL, 5, 'pending'),
(9, '2025-10-15 00:00:00.000000', '11:00:00.000000', NULL, NULL, 10, NULL, 'Đau bụng sau khi ăn hải sản', NULL, 1, 'refunded', NULL, 6, 'cancelled'),
(13, '2025-10-21 08:30:00.000000', '08:30:00.000000', NULL, NULL, 1, NULL, 'Khám tổng quát lần đầu', b'0', 5, 'UNPAID', NULL, 1, 'PENDING'),
(36, '2025-10-21 10:00:00.000000', '10:00:00.000000', NULL, NULL, 1, NULL, 'Cập nhật giờ khám sang 10h sáng', b'0', 5, 'UNPAID', NULL, 1, 'CONFIRMED'),
(40, '2025-10-21 09:00:00.000000', '09:00:00.000000', NULL, NULL, 1, NULL, 'Khám định kỳ buổi sáng', b'0', 5, 'UNPAID', NULL, NULL, 'PENDING'),
(46, '2025-10-21 10:00:00.000000', '09:00:00.000000', NULL, NULL, 1, NULL, 'Khám định kỳ buổi sáng', b'0', 9, 'UNPAID', NULL, NULL, 'PENDING'),
(49, '2025-10-21 10:00:00.000000', '08:00:00.000000', NULL, NULL, 1, NULL, 'Khám tổng quát buổi sáng', b'0', 9, 'UNPAID', NULL, NULL, 'PENDING'),
(50, '2025-10-21 08:00:00.000000', '14:00:00.000000', NULL, NULL, 1, NULL, 'Khám tổng quát buổi chiều', b'0', 9, 'UNPAID', NULL, NULL, 'PENDING');

ALTER TABLE `appointments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;
