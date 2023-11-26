package com.global.project.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8085");
        devServer.setDescription("Server URL in Local environment");
//
//        Server prodServer = new Server();
//        prodServer.setUrl(prodUrl);
//        prodServer.setDescription("Server URL in Production environment");

//        Contact contact = new Contact();
//        contact.setEmail("nguyenquyen5120@gmail.com");
//        contact.setName("Quyen Nguyen");
//        contact.setUrl("https://www.bezkoder.com");

//        License mitLicense = new License().name("CYVN License");

        Info info = new Info()
                .title("Project: CYVN API")
                .version("1.0")
//                .contact(contact)
                .description("CYVN API");
//                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}