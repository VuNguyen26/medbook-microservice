CREATE DATABASE IF NOT EXISTS medbook_userdb;
USE medbook_userdb;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','DOCTOR','PATIENT') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO `users` (`id`, `email`, `name`, `password`, `phone`, `role`) VALUES
(1, 'minhthanh.vo@medbook.com', 'Võ Minh Thành', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0909123456', 'DOCTOR'),
(2, 'khuong.nguyen@medbook.com', 'Dr. Nguyễn Trương Khương', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911001100', 'DOCTOR'),
(3, 'chung.nguyen@medbook.com', 'Dr. Nguyễn Kim Chung', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911002200', 'DOCTOR'),
(4, 'man.vo@medbook.com', 'Dr. Võ Văn Mẫn', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911003300', 'DOCTOR'),
(5, 'baoanh.nguyen@medbook.com', 'Dr. Nguyễn Mỹ Bảo Anh', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911004400', 'DOCTOR'),
(6, 'nhan.hieu@medbook.com', 'Dr. Lê Hoa Hiếu Nhân', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911005500', 'DOCTOR'),
(7, 'chi.lan@medbook.com', 'Dr. Nguyễn Lan Chi', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911006600', 'DOCTOR'),
(8, 'lan.tran@medbook.com', 'Dr. Trần Thị Lan', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911007700', 'DOCTOR'),
(9, 'minh.pham@medbook.com', 'Dr. Phạm Văn Minh', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911008800', 'DOCTOR'),
(10, 'mai.pham@medbook.com', 'Dr. Phạm Thị Mai', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911009900', 'DOCTOR'),
(11, 'hoa.nguyen@medbook.com', 'Dr. Nguyễn Thị Hoa', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911010000', 'DOCTOR'),
(12, 'hang.tran@medbook.com', 'Trần Thị Hằng', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0922001100', 'PATIENT'),
(13, 'vinh.nguyen@medbook.com', 'Nguyễn Quốc Vinh', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0922002200', 'PATIENT'),
(14, 'nhung.le@medbook.com', 'Lê Thị Hồng Nhung', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0922003300', 'PATIENT'),
(15, 'ha.pham@medbook.com', 'Phạm Thị Thu Hà', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0922004400', 'PATIENT'),
(16, 'tam.hoang@medbook.com', 'Hoàng Minh Tâm', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0922005500', 'PATIENT'),
(17, 'huy.vo@medbook.com', 'Võ Đức Huy', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911011100', 'DOCTOR'),
(18, 'dung.tran@medbook.com', 'Trần Quốc Dũng', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911012200', 'DOCTOR'),
(19, 'ha.nguyen@medbook.com', 'Nguyễn Thu Hà', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911013300', 'DOCTOR'),
(20, 'long.phan@medbook.com', 'Phan Hoàng Long', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911014400', 'DOCTOR'),
(21, 'tam.nguyen@medbook.com', 'Nguyễn Minh Tâm', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911015500', 'DOCTOR'),
(22, 'hoa.truong@medbook.com', 'Trương Quang Hòa', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911016600', 'DOCTOR'),
(23, 'ngoc.doan@medbook.com', 'Đoàn Thị Bích Ngọc', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911017700', 'DOCTOR'),
(24, 'hieu.phan@medbook.com', 'Phan Đức Hiếu', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911018800', 'DOCTOR'),
(25, 'thao.nguyen@medbook.com', 'Nguyễn Thanh Thảo', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911019900', 'DOCTOR'),
(26, 'phu.hoang@medbook.com', 'Hoàng Đức Phú', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0911020000', 'DOCTOR'),
(27, 'nhi@example.com', NULL, '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', NULL, 'PATIENT'),
(28, 'admin@medbook.com', 'Admin', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', NULL, 'ADMIN'),
(29, 'leminh@medbook.com', 'Lê Minh Tuấn', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', NULL, 'PATIENT'),
(31, 'A@gmail.com', NULL, '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0123456789', 'PATIENT'),
(32, 'AB@gmail.com', NULL, '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0123456789', 'PATIENT'),
(33, 'abc999@gmail.com', 'Nguyễn Văn A', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0123456799', 'PATIENT'),
(34, 'ten@gmail.com', 'Nguyễn Văn B', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0123456789', 'PATIENT'),
(35, 'abc111@gmail.com', 'Nguyễn Văn A', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0987654321', 'PATIENT'),
(36, 'ac111@gmail.com', 'Nguyễn Văn C', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0987654321', 'PATIENT'),
(37, 'minh.vo@medbook.com', 'Võ Minh Minh', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0909123456', 'DOCTOR'),
(38, 'minhh.vo@medbook.com', 'Võ Minh', '$2a$12$vJLn3FFrPcwDgX4WJrnh2e.vZ5u1TKR8NtwZvw84IFBK8drzJAQLW', '0909123456', 'DOCTOR');

ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;
