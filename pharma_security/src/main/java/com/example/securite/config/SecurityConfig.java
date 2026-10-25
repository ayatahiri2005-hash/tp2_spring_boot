package com.example.securite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                // Accessible sans connexion
                .requestMatchers("/login", "/css/**").permitAll()

                // Du plus précis au plus général : la première règle qui correspond s'applique
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/medicaments/ajouter/**").hasRole("ADMIN")
                .requestMatchers("/medicaments/supprimer/**").hasRole("ADMIN") 
                .requestMatchers("/medicaments/stock/**").hasAnyRole("ADMIN", "PHARMACIEN")
                .requestMatchers("/medicaments/**").hasAnyRole("ADMIN", "PHARMACIEN", "CLIENT")
                .requestMatchers("/ventes/**").hasAnyRole("ADMIN", "PHARMACIEN", "CAISSIER")

                // Tout le reste exige d'être connecté
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}