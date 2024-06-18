package io.mosip.mimoto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = "io.mosip.mimoto.*")
public class TestBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestBootApplication.class, args);
	}
}
