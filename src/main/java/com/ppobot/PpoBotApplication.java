package com.ppobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class PpoBotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(PpoBotApplication.class, args);
    }

}
