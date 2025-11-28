package com.medbook.paymentservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Tắt test contextLoads mặc định để tránh kết nối MySQL khi chạy unit test")
class PaymentServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
