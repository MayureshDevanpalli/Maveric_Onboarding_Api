package com.example.ExampleApiDemo;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class ExampleApiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExampleApiDemoApplication.class, args);
		try {
			String ipAddress = InetAddress.getLocalHost().getHostAddress();
			String port = System.getProperty("server.port", "8080");
			log.info("application is running on http://{}:{}", ipAddress, port);
		} catch (UnknownHostException e) {
			log.error("Error while running application", e.getLocalizedMessage());
		}
	}

}
