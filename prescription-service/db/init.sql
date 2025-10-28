CREATE DATABASE IF NOT EXISTS medbook_prescriptiondb;
USE medbook_prescriptiondb;

-- --------------------------------------------------------
-- Table structure for table `prescriptions`
-- --------------------------------------------------------
CREATE TABLE `prescriptions` (
  `id` bigint(20) NOT NULL,
  `doctor_id` bigint(20) NOT NULL,
  `issued_at` datetime(6) NOT NULL,
  `medical_record_id` bigint(20) NOT NULL,
  `notes` text DEFAULT NULL,
  `patient_id` bigint(20) NOT NULL,
  `status` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Insert data into table `prescriptions`
-- --------------------------------------------------------
INSERT INTO `prescriptions` (`id`, `doctor_id`, `issued_at`, `medical_record_id`, `notes`, `patient_id`, `status`) VALUES
(1, 3, '2025-10-15 10:00:00.000000', 1, 'Bệnh nhân cần uống 2 viên/ngày', 2, 'ACTIVE');

ALTER TABLE `prescriptions`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
