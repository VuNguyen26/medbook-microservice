CREATE DATABASE IF NOT EXISTS medbook_doctordb;
USE medbook_doctordb;

CREATE TABLE `doctors` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `experience` int(11) DEFAULT NULL,
  `fee` double DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `specialty` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `doctors` (`id`, `email`, `experience`, `fee`, `image_url`, `name`, `phone`, `rating`, `specialty`, `title`) VALUES
(1, 'khuong.nguyen@medbook.com', NULL, 300000, NULL, 'Nguyễn Trương Khương', '0911001100', 4.9, NULL, 'ThS.BS.CKII'),
(2, 'chung.nguyen@medbook.com', NULL, 350000, NULL, 'Nguyễn Kim Chung', '0911002200', 4.8, NULL, 'TS.BS'),
(3, 'man.vo@medbook.com', NULL, 320000, NULL, 'Võ Văn Mẫn', '0911003300', 4.7, NULL, 'BS.CKII'),
(4, 'baoanh.nguyen@medbook.com', NULL, 400000, NULL, 'Nguyễn Mỹ Bảo Anh', '0911004400', 4.9, NULL, 'PGS.TS.BS'),
(5, 'nhan.hieu@medbook.com', NULL, 250000, NULL, 'Lê Hoa Hiếu Nhân', '0911005500', 4.8, NULL, 'BS.CKI'),
(6, 'chi.lan@medbook.com', NULL, 200000, NULL, 'Nguyễn Lan Chi', '0911006600', 4.9, NULL, 'ThS.BS'),
(7, 'lan.tran@medbook.com', NULL, 280000, NULL, 'Trần Thị Lan', '0911007700', 4.9, NULL, 'BS.CKI'),
(8, 'minh.pham@medbook.com', NULL, 220000, NULL, 'Phạm Văn Minh', '0911008800', 4.8, NULL, 'ThS.BS'),
(9, 'mai.pham@medbook.com', NULL, 330000, NULL, 'Phạm Thị Mai', '0911009900', 4.8, NULL, 'BS.CKII'),
(10, 'hoa.nguyen@medbook.com', NULL, 260000, NULL, 'Nguyễn Thị Hoa', '0911010000', 4.7, NULL, 'BS.CKI'),
(11, 'huy.vo@medbook.com', NULL, 240000, NULL, 'Võ Đức Huy', '0911011100', 4.8, NULL, 'BS.CKI'),
(12, 'dung.tran@medbook.com', NULL, 310000, NULL, 'Trần Quốc Dũng', '0911012200', 4.7, NULL, 'ThS.BS'),
(13, 'ha.nguyen@medbook.com', NULL, 290000, NULL, 'Nguyễn Thu Hà', '0911013300', 4.9, NULL, 'BS.CKII'),
(14, 'long.phan@medbook.com', NULL, 340000, NULL, 'Phan Hoàng Long', '0911014400', 4.9, NULL, 'TS.BS'),
(15, 'tam.nguyen@medbook.com', NULL, 270000, NULL, 'Nguyễn Minh Tâm', '0911015500', 4.8, NULL, 'ThS.BS'),
(16, 'hoa.truong@medbook.com', NULL, 230000, NULL, 'Trương Quang Hòa', '0911016600', 4.7, NULL, 'BS.CKI'),
(17, 'ngoc.doan@medbook.com', NULL, 260000, NULL, 'Đoàn Thị Bích Ngọc', '0911017700', 4.9, NULL, 'BS.CKI'),
(18, 'hieu.phan@medbook.com', NULL, 320000, NULL, 'Phan Đức Hiếu', '0911018800', 4.8, NULL, 'BS.CKII'),
(19, 'thao.nguyen@medbook.com', NULL, 280000, NULL, 'Nguyễn Thanh Thảo', '0911019900', 4.8, NULL, 'BS.CKI'),
(20, 'phu.hoang@medbook.com', NULL, 300000, NULL, 'Hoàng Đức Phú', '0911020000', 4.9, NULL, 'ThS.BS');

ALTER TABLE `doctors`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
