package com.lws.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Slf4j
class DemoAppTests {

	@Test
	void testOnePlusOneEqualsTwo() {
		log.info("i am testing 1 + 1 = 2");

		Assertions.assertTrue((1 + 1 == 2));
	}

	@Test
	void testOnePlusTwoEqualsThree() {
		log.info("i am testing 1 + 2 = 3");

		Assertions.assertTrue((1 + 2 == 3));
	}

}
