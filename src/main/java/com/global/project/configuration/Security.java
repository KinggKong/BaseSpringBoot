package com.global.project.configuration;

import com.global.project.configuration.exception.Forbidden;
import com.global.project.configuration.exception.Unauthorized;
import com.global.project.configuration.jwtConfig.JwtAuthenticationFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class Security {
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    Unauthorized unauthorized;
    @Autowired
    Forbidden forbidden;
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public WebMvcConfigurer configurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Áp dụng cho tất cả các đường dẫn
                        .allowedOrigins(allowedOrigins) // Các domain được phép
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Các phương thức HTTP được phép
                        .allowedHeaders("*") // Cho phép tất cả các headers
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http
                .authorizeHttpRequests((requests) -> {
                            try {
                                requests
                                        .requestMatchers(new AntPathRequestMatcher("/public/**"),
                                                new AntPathRequestMatcher("/error"),
                                                new AntPathRequestMatcher("/images/**"),
                                                new AntPathRequestMatcher("/swagger-ui/**"),
                                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                                new AntPathRequestMatcher("/v1/api/auth/**"),
                                                new AntPathRequestMatcher("/v1/api/videos/**"),
                                                new AntPathRequestMatcher("/v1/api/accounts/refresh-token"),
                                                new AntPathRequestMatcher("/ws/**")
//                                                new AntPathRequestMatcher("/ws/info"),
//                                                new AntPathRequestMatcher("/**")

                                        )
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated()
                                        .and()
                                        .exceptionHandling()
                                        .authenticationEntryPoint(unauthorized)
                                        .accessDeniedHandler(forbidden)
                                        .and()
                                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .httpBasic();
        return http.build();
    }

}
