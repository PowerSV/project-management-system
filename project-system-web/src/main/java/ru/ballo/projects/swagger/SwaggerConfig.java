package ru.ballo.projects.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    String description = """
            Движок, который повзоляет управлять проектами, задачами внутри проекта,\s
            командами, которые работают над проектом и сотрудниками.""";

    @Bean
    public OpenAPI OpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Система управлениями проектами")
                        .description(description)
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("Ballo Alexey")
                                .email("ballo.aleksej@gmail.com")))
                ;
    }

}
