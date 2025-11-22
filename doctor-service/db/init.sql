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
-- 1–3: ĐA KHOA
(1, 'khuong.nguyen@medbook.com', NULL, 300000, '/doctors/A.jpg', 'Nguyễn Trương Khương', '0911001100', 4.9, 'đa khoa', 'ThS.BS.CKII'),
(2, 'chung.nguyen@medbook.com', NULL, 350000, '/doctors/B.jpg', 'Nguyễn Kim Chung', '0911002200', 4.8, 'đa khoa', 'TS.BS'),
(3, 'man.vo@medbook.com', NULL, 320000, '/doctors/C.jpg', 'Võ Văn Mẫn', '0911003300', 4.7, 'đa khoa', 'BS.CKII'),

-- 4–6: PHỤ KHOA
(4, 'baoanh.nguyen@medbook.com', NULL, 400000, '/doctors/D.jpg', 'Nguyễn Mỹ Bảo Anh', '0911004400', 4.9, 'phụ khoa', 'PGS.TS.BS'),
(5, 'nhan.hieu@medbook.com', NULL, 250000, '/doctors/E.jpg', 'Lê Hoa Hiếu Nhân', '0911005500', 4.8, 'phụ khoa', 'BS.CKI'),
(6, 'chi.lan@medbook.com', NULL, 200000, '/doctors/G.jpg', 'Nguyễn Lan Chi', '0911006600', 4.9, 'phụ khoa', 'ThS.BS'),

-- 7–9: DA LIỄU
(7, 'lan.tran@medbook.com', NULL, 280000, '/doctors/H.jpg', 'Trần Thị Lan', '0911007700', 4.9, 'da liễu', 'BS.CKI'),
(8, 'minh.pham@medbook.com', NULL, 220000, '/doctors/I.jpg', 'Phạm Văn Minh', '0911008800', 4.8, 'da liễu', 'ThS.BS'),
(9, 'mai.pham@medbook.com', NULL, 330000, '/doctors/K.jpg', 'Phạm Thị Mai', '0911009900', 4.8, 'da liễu', 'BS.CKII'),

-- 10–12: NHI KHOA
(10, 'hoa.nguyen@medbook.com', NULL, 260000, '/doctors/L.jpg', 'Nguyễn Thị Hoa', '0911010000', 4.7, 'nhi', 'BS.CKI'),
(11, 'huy.vo@medbook.com', NULL, 240000, '/doctors/M.jpg', 'Võ Đức Huy', '0911011100', 4.8, 'nhi', 'BS.CKI'),
(12, 'dung.tran@medbook.com', NULL, 310000, '/doctors/N.jpg', 'Trần Quốc Dũng', '0911012200', 4.7, 'nhi', 'ThS.BS'),

-- 13–15: THẦN KINH
(13, 'ha.nguyen@medbook.com', NULL, 290000, '/doctors/O.jpg', 'Nguyễn Thu Hà', '0911013300', 4.9, 'thần kinh', 'BS.CKII'),
(14, 'long.phan@medbook.com', NULL, 340000, '/doctors/P.jpg', 'Phan Hoàng Long', '0911014400', 4.9, 'thần kinh', 'TS.BS'),
(15, 'tam.nguyen@medbook.com', NULL, 270000, '/doctors/Q.jpg', 'Nguyễn Minh Tâm', '0911015500', 4.8, 'thần kinh', 'ThS.BS'),

-- 16–20: TIÊU HÓA
(16, 'hoa.truong@medbook.com', NULL, 230000, '/doctors/R.jpg', 'Trương Quang Hòa', '0911016600', 4.7, 'tiêu hóa', 'BS.CKI'),
(17, 'ngoc.doan@medbook.com', NULL, 260000, '/doctors/S.jpg', 'Đoàn Thị Bích Ngọc', '0911017700', 4.9, 'tiêu hóa', 'BS.CKI'),
(18, 'hieu.phan@medbook.com', NULL, 320000, '/doctors/T.jpg', 'Phan Đức Hiếu', '0911018800', 4.8, 'tiêu hóa', 'BS.CKII'),
(19, 'thao.nguyen@medbook.com', NULL, 280000, '/doctors/U.jpg', 'Nguyễn Thanh Thảo', '0911019900', 4.8, 'tiêu hóa', 'BS.CKI'),
(20, 'phu.hoang@medbook.com', NULL, 300000, '/doctors/V.jpg', 'Hoàng Đức Phú', '0911020000', 4.9, 'tiêu hóa', 'ThS.BS');

ALTER TABLE `doctors`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
