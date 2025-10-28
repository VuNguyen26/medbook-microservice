CREATE DATABASE IF NOT EXISTS medbook_notificationdb;
USE medbook_notificationdb;

-- --------------------------------------------------------
-- Table structure for table `notifications`
-- --------------------------------------------------------
CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `type` enum('APPOINTMENT','PAYMENT','REMINDER','SYSTEM') DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `notifications` (`id`, `created_at`, `is_read`, `message`, `title`, `type`, `user_id`) VALUES
(1, '2025-10-06 21:51:36.000000', b'0', 'Lịch hẹn với bác sĩ Nguyễn Trương Khương vào ngày 2025-10-07 lúc 08:00 đã được xác nhận.', 'Xác nhận lịch hẹn', NULL, 12),
(2, '2025-10-04 21:51:36.000000', b'1', 'Bạn đã thanh toán 300.000đ cho lịch hẹn với bác sĩ Nguyễn Kim Chung.', 'Thanh toán thành công', NULL, 13),
(3, '2025-10-05 21:51:36.000000', b'0', 'Kết quả khám bệnh của bạn đã được cập nhật trong hồ sơ y tế.', 'Kết quả khám bệnh', NULL, 14),
(4, '2025-10-03 21:51:36.000000', b'0', 'Bạn đã đặt lịch hẹn với bác sĩ Võ Văn Mẫn lúc 10:00 ngày 2025-10-10.', 'Đặt lịch thành công', NULL, 15),
(6, '2025-10-06 21:51:36.000000', b'0', 'Bạn có lịch hẹn mới với bệnh nhân Trần Thị Hằng vào ngày 2025-10-08 lúc 09:00.', 'Lịch hẹn mới', NULL, 2),
(7, '2025-10-02 21:51:36.000000', b'1', 'Bệnh nhân Nguyễn Quốc Vinh đã hủy lịch hẹn ngày 2025-10-09.', 'Lịch hẹn đã bị hủy', NULL, 3),
(8, '2025-10-04 21:51:36.000000', b'1', 'Bệnh nhân Phạm Thị Mai đã hoàn tất thanh toán phí khám.', 'Thanh toán hoàn tất', NULL, 4),
(9, '2025-10-06 21:51:36.000000', b'0', 'Bạn vừa nhận được đánh giá 5 sao từ bệnh nhân Nguyễn Thị Hoa.', 'Đánh giá mới', NULL, 5),
(14, '2025-10-17 23:40:02.000000', b'0', 'Bác sĩ đã xác nhận lịch hẹn', 'Lịch hẹn mới', 'APPOINTMENT', 1),
(15, '2025-10-18 00:25:14.000000', b'0', 'Bác sĩ đã xác nhận lịch hẹn', 'Lịch hẹn mới', 'APPOINTMENT', 5),
(16, '2025-10-18 00:29:30.000000', b'0', 'Bác sĩ đã xác nhận lịch hẹn', 'Lịch hẹn mới', 'APPOINTMENT', 1),
(17, '2025-10-18 00:30:49.000000', b'0', 'Lịch hẹn ngày 2025-10-25', 'Đặt lịch thành công', 'APPOINTMENT', 5),
(18, '2025-10-18 00:38:36.000000', b'0', 'Lịch hẹn ngày 2025-10-25', 'Đặt lịch thành công', 'APPOINTMENT', 5),
(19, '2025-10-18 00:44:05.000000', b'0', 'Hóa đơn #123 đã được thanh toán', 'Thanh toán thành công', 'PAYMENT', 5);

ALTER TABLE `notifications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

-- --------------------------------------------------------
-- Table structure for table `notification_log`
-- --------------------------------------------------------
CREATE TABLE `notification_log` (
  `id` bigint(20) NOT NULL,
  `error_message` varchar(500) DEFAULT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `success` bit(1) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `notification_log` (`id`, `error_message`, `sent_at`, `success`, `type`, `user_id`) VALUES
(1, NULL, '2025-10-18 00:25:14.000000', b'1', 'APPOINTMENT', 5),
(2, NULL, '2025-10-18 00:29:30.000000', b'1', 'APPOINTMENT', 1),
(3, NULL, '2025-10-18 00:30:49.000000', b'1', 'APPOINTMENT', 5),
(4, NULL, '2025-10-18 00:38:36.000000', b'1', 'APPOINTMENT', 5),
(5, NULL, '2025-10-18 00:44:05.000000', b'1', 'PAYMENT', 5);

ALTER TABLE `notification_log`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
