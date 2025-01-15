package com.global.project.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    private static final String SCHEME_NAME = "Token";
    private static final String SCHEME = "Bearer";

    @Value("${facebook.openapi.staging-url}")
    private String stagingUrl;

    @Bean
    public OpenAPI OpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Local environment");


        Server stagingServer = new Server();
        stagingServer.setUrl(stagingUrl);
        stagingServer.setDescription("Server URL in Staging environment");

//        Contact contact = new Contact();
//        contact.setEmail("nguyenquyen5120@gmail.com");
//        contact.setName("Quyen Nguyen");
//        contact.setUrl("https://www.bezkoder.com");

//        License mitLicense = new License().name("CYVN License");

        Info info = new Info()
                .title("Project: CYVN API - Line 2")
                .version("1.0")
//                .contact(contact)
                .description("CYVN API - Line 2");
//                .license(mitLicense);
        OpenAPI openApi = new OpenAPI().info(info).servers(List.of(stagingServer, devServer));
        addSecurity(openApi);
        return openApi;
    }

    private void addSecurity(OpenAPI openApi) {
        var components = createComponents();
        var securityItem = new SecurityRequirement().addList(SCHEME_NAME);
        openApi.components(components).addSecurityItem(securityItem);
    }

    private Components createComponents() {
        var components = new Components();
        components.addSecuritySchemes(SCHEME_NAME, createSecurityScheme());

        return components;
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME);
    }

}