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
(1, 'minhthanh.vo@medbook.com', 'Võ Minh Thành', '$2a$10$52ICNjxCVpThCn.ZF1SRWuAjTSJk5dV/QUqCmJqVNqIbNpPJxHyTq', '0909123456', 'DOCTOR'),
(2, 'khuong.nguyen@medbook.com', 'Dr. Nguyễn Trương Khương', '$2a$10$zHqBGqdbWRcYJWKvRrBpQuY6b1jburH1QK.4aQUjpwj7x4Ydg19XG', '0911001100', 'DOCTOR'),
(3, 'chung.nguyen@medbook.com', 'Dr. Nguyễn Kim Chung', '$2a$10$mTdhbgVQ8vnGZTAVQp4jXOgWTI9bKd8Tac5R1twhOTyPSzMM8e/dy', '0911002200', 'DOCTOR'),
(4, 'man.vo@medbook.com', 'Dr. Võ Văn Mẫn', '$2a$10$iQ61BVMEtrPCePpDejOvfOYv0QCH25/hc3AgIEypZ9r/okq.pIgRW', '0911003300', 'DOCTOR'),
(5, 'baoanh.nguyen@medbook.com', 'Dr. Nguyễn Mỹ Bảo Anh', '$2a$10$NUQ0W8NN99W09KzfOJjT1uuKCTx/3IQ7BcnLruTtDEikfBra1jjYy', '0911004400', 'DOCTOR'),
(6, 'nhan.hieu@medbook.com', 'Dr. Lê Hoa Hiếu Nhân', '$2a$10$66EjufK2X7Hbjw/jipcGfeL9bbbvboTjVannbIMM7.iYZKq0pXHTO', '0911005500', 'DOCTOR'),
(7, 'chi.lan@medbook.com', 'Dr. Nguyễn Lan Chi', '$2a$10$mNZ2VrA1r.0a33bPJxIEYeEKQjPUnAyP76AF9rjsyTt3Ra2XiFeFG', '0911006600', 'DOCTOR'),
(8, 'lan.tran@medbook.com', 'Dr. Trần Thị Lan', '$2a$10$GAK9ECYLMRQwdsWaUOa0j.1iJAgxsv.QUficzTIwm8e0Tny9vq/qm', '0911007700', 'DOCTOR'),
(9, 'minh.pham@medbook.com', 'Dr. Phạm Văn Minh', '$2a$10$M4YsX66Vj9UkKgbnJR.E5O5foF38.stNZRjKi11zXURwfrtXMbfc6', '0911008800', 'DOCTOR'),
(10, 'mai.pham@medbook.com', 'Dr. Phạm Thị Mai', '$2a$10$tBdVos7ZH9EyDr3PQiE9yeCRmPbAOdSA3H8W3WG8A66P5oBnTxIn.', '0911009900', 'DOCTOR'),
(11, 'hoa.nguyen@medbook.com', 'Dr. Nguyễn Thị Hoa', '$2a$10$geQ54yGhEmoXYXEAB0SpP.b2cX9TdxHwPYz17EfjfM2xvJXZI/Gs2', '0911010000', 'DOCTOR'),
(12, 'hang.tran@medbook.com', 'Trần Thị Hằng', '$2a$10$khUpCp6tvWnqj475xYg3qeEWw/RY6p3Ntbghqn.ydZJnmBq9bKbTK', '0922001100', 'PATIENT'),
(13, 'vinh.nguyen@medbook.com', 'Nguyễn Quốc Vinh', '$2a$10$vYQ./WXEhVdFJIreQZ4ALuIN0Wv4sX42UpgLonIanwB43EliHLNRW', '0922002200', 'PATIENT'),
(14, 'nhung.le@medbook.com', 'Lê Thị Hồng Nhung', '$2a$10$DfcyLbn/p67Og4B7jLm69.lCEXMpJUTLQLUslaB.MQXDS6qQ0ryL2', '0922003300', 'PATIENT'),
(15, 'ha.pham@medbook.com', 'Phạm Thị Thu Hà', '$2a$10$QIsPiRLiKeN14.DP86nwwuFHDW1nkCLHB4Fmg.lWaIbx0LBvBIeyi', '0922004400', 'PATIENT'),
(16, 'tam.hoang@medbook.com', 'Hoàng Minh Tâm', '$2a$10$SzgfnF5w9hBRU59cEURb8.5s/vXgab.yTZmc5FTTeBWwSPpkPERQa', '0922005500', 'PATIENT'),
(17, 'huy.vo@medbook.com', 'Võ Đức Huy', '$2a$10$9xg2jSekipUWgHzOIRI6eeUT7Pxn6AT91ryaxtcFaWOP0sOQThXkW', '0911011100', 'DOCTOR'),
(18, 'dung.tran@medbook.com', 'Trần Quốc Dũng', '$2a$10$JUYeoXV9iAzz7j34XVSVmOPGKG6O4L4PD7ju1vr1ZCsT8YI12B8JK', '0911012200', 'DOCTOR'),
(19, 'ha.nguyen@medbook.com', 'Nguyễn Thu Hà', '$2a$10$JT.bC./mA55CeBYEOMNEZOzKi.J7JtXyyoTWvJ6Y4e.dbaIAnNsj6', '0911013300', 'DOCTOR'),
(20, 'long.phan@medbook.com', 'Phan Hoàng Long', '$2a$10$LFPFi7MraOT0pFm7r8f4ouNlvM382FDj0cKMZ/YpFuHj8cYL/aqGy', '0911014400', 'DOCTOR'),
(21, 'tam.nguyen@medbook.com', 'Nguyễn Minh Tâm', '$2a$10$Wee9LqqhugQcuO/8b83lYe93011TTknga93YMdSInvYGv0YDr89X2', '0911015500', 'DOCTOR'),
(22, 'hoa.truong@medbook.com', 'Trương Quang Hòa', '$2a$10$PF1ua4dcMWxKrUXpfePsZeFEKhBsuoE2cW7EPXdlGCYxJTbxKijYC', '0911016600', 'DOCTOR'),
(23, 'ngoc.doan@medbook.com', 'Đoàn Thị Bích Ngọc', '$2a$10$yKJg6vzkEPDx3zevob7..OEQ8zbndoxLvicnmKSuHq4nC7kxBVV8S', '0911017700', 'DOCTOR'),
(24, 'hieu.phan@medbook.com', 'Phan Đức Hiếu', '$2a$10$21z1xG/sfmMdXPWQWRAuk.TwiGUnB9ag8xc/VNigbMfEOtF6hS0gK', '0911018800', 'DOCTOR'),
(25, 'thao.nguyen@medbook.com', 'Nguyễn Thanh Thảo', '$2a$10$CLo5gfK9P6K9EjCgfrZUgOvHYYWu5KwjaVTx3nZqYHheMlMvOY83K', '0911019900', 'DOCTOR'),
(26, 'phu.hoang@medbook.com', 'Hoàng Đức Phú', '$2a$10$gqtaWG19i4KhBKuGJpAk7u4JwqVSUUX/jZijjlPhWJUQ7l6eLGCNO', '0911020000', 'DOCTOR'),
(27, 'nhi@example.com', NULL, '$2a$10$7xC5pO.3fRX0etqdhlqdVunSFKMDsApK3fE.VHbtyCS705WXOcb72', NULL, 'PATIENT'),
(28, 'admin@medbook.com', 'Admin', '$2a$10$6f.3DL2SOEWiS9Sdc.U65.VdDbQjbOO8mgA14Jlq4e1zeSyfrBsii', NULL, 'ADMIN'),
(29, 'leminh@medbook.com', 'Lê Minh Tuấn', '$2a$10$IEiHRxQwo5GpLdzXEm5HNeGKE4MljOLovzl33roh9AgsgtaxMjs0K', NULL, 'PATIENT'),
(31, 'A@gmail.com', NULL, '$2a$10$c2pCT26PUJQ/S2/7IlaZp..ITgBSiP7kOZxbtafbc3by8ta8H.iwe', '0123456789', 'PATIENT'),
(32, 'AB@gmail.com', NULL, '$2a$10$smJXITPREewXVBx9ZKQbOe8beGWVzlpqRSGr.q9pfIG8InNTvvbOq', '0123456789', 'PATIENT'),
(33, 'abc999@gmail.com', 'Nguyễn Văn A', '$2a$10$g4xzdEtQR/MVyzTux6uge.VDDHr9CKHKeU5j4N/NsLYRxXrbfQRRi', '0123456799', 'PATIENT'),
(34, 'ten@gmail.com', 'Nguyễn Văn B', '$2a$10$531Bn5wf7Pxz8ziAmpKCMO9sBcc5E2/RQ1Hm8TLLfXDqRT.TXd4iy', '0123456789', 'PATIENT'),
(35, 'abc111@gmail.com', 'Nguyễn Văn A', '$2a$10$dPNsrNBtrahxSsBp9dc0DOhbbppFlNjx8w0PrcI7L4qD8UJvegwza', '0987654321', 'PATIENT'),
(36, 'ac111@gmail.com', 'Nguyễn Văn C', '$2a$10$tanvi.4XWFyHer4JUbtxe.TNOLkCqKwxcUimm1Ab/PytynG5onRFm', '0987654321', 'PATIENT'),
(37, 'minh.vo@medbook.com', 'Võ Minh Minh', '$2a$10$Wg3icBITTTFKa5wH5EruLOIRNqo04B0TX1d0Wnte8pNfHEeeNjwgO', '0909123456', 'DOCTOR'),
(38, 'minhh.vo@medbook.com', 'Võ Minh', '$2a$10$AVq4mCIBhszeREgrIvAgG.AUrUtneQAMwrFILJBcsZ9wjOpx/EfCW', '0909123456', 'DOCTOR');

ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;
