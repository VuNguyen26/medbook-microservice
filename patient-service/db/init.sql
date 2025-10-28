CREATE DATABASE IF NOT EXISTS medbook_patientdb;
USE medbook_patientdb;

-- --------------------------------------------------------
-- Table structure for table `patients`
-- --------------------------------------------------------
CREATE TABLE `patients` (
  `id` bigint(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------
-- Dumping data for table `patients`
-- --------------------------------------------------------
INSERT INTO `patients` (`id`, `address`, `age`, `email`, `full_name`, `gender`, `phone`) VALUES
(1, '273 An Dương Vương, Q.5, TP.HCM', 30, 'hang.tran@medbook.com', 'Trần Thị Hằng', 'female', '0922001100'),
(2, '12 Nguyễn Trãi, Q.1, TP.HCM', 33, 'vinh.nguyen@medbook.com', 'Nguyễn Quốc Vinh', 'female', '0922002200'),
(3, '45 Lê Lợi, Q.1, TP.HCM', 27, 'nhung.le@medbook.com', 'Lê Thị Hồng Nhung', 'female', '0922003300'),
(4, '68 Cách Mạng Tháng 8, Q.10, TP.HCM', 36, 'ha.pham@medbook.com', 'Phạm Thị Thu Hà', 'female', '0922004400'),
(5, '120 Nguyễn Văn Cừ, Q.5, TP.HCM', 35, 'tam.hoang@medbook.com', 'Hoàng Minh Tâm', 'male', '0922005500'),
(9, NULL, 25, 'tam.hoang@medbook.com', NULL, 'Nam', '0901234567'),
(10, NULL, 24, 'ha.pham@medbook.com', NULL, 'Nữ', '0901234567'),
(11, NULL, 24, 'hong.pham@medbook.com', NULL, 'Nữ', '0901234567');

ALTER TABLE `patients`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
