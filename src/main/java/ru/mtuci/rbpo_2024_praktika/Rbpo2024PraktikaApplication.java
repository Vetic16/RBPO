package ru.mtuci.rbpo_2024_praktika;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Включаем поддержку планировщика
public class Rbpo2024PraktikaApplication {

    public static void main(String[] args) {
        SpringApplication.run(Rbpo2024PraktikaApplication.class, args);
    }

}
