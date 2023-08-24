package com.lws.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;

@SpringBootApplication

@RestController
@Slf4j
public class DemoApp {

	public static void main(String[] args) {
		SpringApplication.run(DemoApp.class, args);
	}

	@GetMapping("/hello")
	public String hello() throws Exception {

		InetAddress address = InetAddress.getLocalHost();

		log.info("receive a hello request: " + address.getHostName() + " : " + address.getHostAddress());
		return address.getHostName() + " : " + address.getHostAddress() + " say hello -> v2\n";
	}

}
