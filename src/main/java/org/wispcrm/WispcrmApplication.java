package org.wispcrm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling

public class WispcrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(WispcrmApplication.class, args);
	}

}
