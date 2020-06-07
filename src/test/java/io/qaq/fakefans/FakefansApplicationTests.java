package io.qaq.fakefans;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FakefansApplicationTests {

	@Value("${qrcode.image.path}")
	private String url;

	@Test
	void contextLoads() {

	}

	@Test
	void getUrl() {
		System.out.println(url);
	}

}
