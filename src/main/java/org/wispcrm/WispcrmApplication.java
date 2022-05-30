package org.wispcrm;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WispcrmApplication {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(WispcrmApplication.class, args);
    }

//	@Scheduled(cron = "5 * * * * *")
    private void showDate() {
        System.out.println(LocalDateTime.now());
    }
}
