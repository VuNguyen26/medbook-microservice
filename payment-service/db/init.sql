CREATE DATABASE IF NOT EXISTS medbook_paymentdb;
USE medbook_paymentdb;

-- --------------------------------------------------------
-- Table structure for table `payments`
-- --------------------------------------------------------
CREATE TABLE `payments` (
  `id` bigint(20) NOT NULL,
  `amount` double DEFAULT NULL,
  `appointment_id` bigint(20) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `patient_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `transaction_code` varchar(255) DEFAULT NULL,
  `transaction_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Insert data into table `payments`
-- --------------------------------------------------------
INSERT INTO `payments` (`id`, `amount`, `appointment_id`, `method`, `note`, `patient_id`, `status`, `transaction_code`, `transaction_time`) VALUES
(1, 300000, 1, 'momo', NULL, NULL, 'completed', NULL, NULL),
(2, 350000, 2, 'credit_card', NULL, NULL, 'pending', NULL, NULL),
(3, 320000, 3, 'cash', NULL, NULL, 'completed', NULL, NULL),
(4, 400000, 4, 'insurance', NULL, NULL, 'completed', NULL, NULL),
(5, 250000, 5, 'cash', NULL, NULL, 'pending', NULL, NULL),
(6, 280000, 6, 'momo', NULL, NULL, 'completed', NULL, NULL),
(7, 220000, 7, 'cash', NULL, NULL, 'completed', NULL, NULL),
(8, 330000, 8, 'credit_card', NULL, NULL, 'pending', NULL, NULL),
(9, 260000, 9, 'momo', NULL, NULL, 'failed', NULL, NULL),
(10, 200000, 57, 'momo', NULL, 2, 'completed', NULL, '2025-10-17 22:43:36.000000'),
(11, 200000, 57, 'momo', NULL, 2, 'completed', NULL, '2025-10-17 22:56:43.000000');

ALTER TABLE `payments`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
