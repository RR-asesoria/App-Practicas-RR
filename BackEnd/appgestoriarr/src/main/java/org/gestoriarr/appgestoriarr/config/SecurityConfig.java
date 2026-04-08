package org.gestoriarr.appgestoriarr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private FirebaseAuthenticationFilter firebaseAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(firebaseAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/clientesHistorico/ADMIN/**").hasRole("ADMIN")
                        .requestMatchers("/api/clientesHistorico/USERBASE/**").authenticated()

                        .requestMatchers("/api/clientes/ADMIN/**").hasRole("ADMIN")
                        .requestMatchers("/api/clientes/USERBASE/**").authenticated()

                        .requestMatchers("/api/clientes/excel/ADMIN/**").hasRole("ADMIN")
                        .requestMatchers("/api/clientes/excel/USERBASE/**").authenticated()

                        .requestMatchers("/api/user/ADMIN/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/USERBASE/**").authenticated()
                        .anyRequest().permitAll()
                );

        return http.build();
    }

}