package com.htc.incidentmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

        private final CustomAccessDeniedHandler customAccessDeniedHandler;

        private final JwtAuthFilter jwtAuthFilter;

        public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomAccessDeniedHandler customAccessDeniedHandler) {
                this.jwtAuthFilter = jwtAuthFilter;
                this.customAccessDeniedHandler = customAccessDeniedHandler;
        }

        @Bean
        public SecurityFilterChain filterChain(
                        org.springframework.security.config.annotation.web.builders.HttpSecurity http)
                        throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                // Swagger public access
                                                .requestMatchers("/api-docs",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/api-docs/swagger-config")
                                                .permitAll()
                                                .requestMatchers("/actuator/**", "/favicon.ico/**").permitAll()

                                                // Authentication endpoints
                                                .requestMatchers("/api/auth/**").permitAll()

                                                // Admin-only endpoints
                                                .requestMatchers(
                                                                "/api/slas/**",
                                                                "/api/categories/**",
                                                                "/api/health/**")
                                                .hasRole("ADMIN")

                                                // Tickets & attachments accessible to all roles
                                                .requestMatchers(
                                                                "/api/tickets/**",
                                                                "/api/attachments/**", "/api/ticket-assignments/**")
                                                .hasAnyRole("ADMIN", "AGENT", "USER", "MANAGER")

                                                .anyRequest().hasRole("ADMIN"))
                                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(ex -> ex
                                                .accessDeniedHandler(customAccessDeniedHandler));

                // Add JWT filter
                http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
