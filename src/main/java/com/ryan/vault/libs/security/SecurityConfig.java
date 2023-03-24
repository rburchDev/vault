package com.ryan.vault.libs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "**/swagger-ui/index.html",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                        .authorizeHttpRequests(a -> a
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                        )
                                .httpBasic()
                                        .and()
                .build();
    }

}
