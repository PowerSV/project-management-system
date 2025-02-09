package ru.ballo.projects;

import ru.ballo.projects.email.config.EmailConfig;
import ru.ballo.projects.security.config.SecurityConfig;
import ru.ballo.projects.swagger.SwaggerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootApplication
@Import({SecurityConfig.class, SwaggerConfig.class, EmailConfig.class})
public class MainApp {

    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }
}
