package com.example.foodle.auth.config;

import com.example.foodle.auth.jwt.JwtTokenFilter;
import com.example.foodle.auth.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService manager;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                            "/users/signin",
                            "/users/signup",
                            "users/signup-owner"
                    )
                    .anonymous();
                    auth.requestMatchers(
                            "/users/update",
                            "/users/profile",
                            "/users/get-user-info"
                    )
                    .authenticated();
                    auth.requestMatchers(
                            "/admin",
                            "/admin/**"
                    )
                    .hasRole("ADMIN");
                    auth.requestMatchers(
                            "/restaurant/**"
                    )
                    .hasRole("OWNER");
                    auth.requestMatchers("/error", "/static/**", "/views/**", "/")
                            .permitAll();
                    auth.anyRequest()
                            .hasRole("ACTIVE");
                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtTokenFilter(
                                jwtTokenUtils,
                                manager
                        ),
                        AuthorizationFilter.class
                );


        return http.build();
    }
}
