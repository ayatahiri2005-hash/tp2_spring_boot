package com.example.exemple_cours_security.configuration;
import ma.ensaf.controller.HomeController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final HomeController homeController;

    SecurityConfig(HomeController homeController) {
        this.homeController = homeController;
    }
	
	// Définir les utilisateurs en mémoire
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		
    	InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		
		manager.createUser(User.withUsername("user")
				.password(passwordEncoder.encode("user"))
				.roles("USER")
				.build()
				);
		// Pour tester et afficher le mot de passe crypté
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encoded = encoder.encode("admin");
		System.out.println("Mot de passe : " + passwordEncoder().encode("admin"));
		System.out.println("Matches pass : " + encoder.matches("admin", encoded));
		
		manager.createUser(User.withUsername("admin")
				.password(passwordEncoder.encode("admin"))
				.roles("ADMIN")
				.build()
				);
		
		return manager;
	}
	

    // Encodeur de mot de passe
    @Bean
    PasswordEncoder passwordEncoder() {
		// return NoOpPasswordEncoder.getInstance(); // aucun encodage
    	return new BCryptPasswordEncoder(); // Pour crypter les mots de passe
	}

    // Configuration des règles de sécurité
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http
			.authorizeHttpRequests(auth -> auth
					.requestMatchers("/login", "/css/**").permitAll() 	//libre accès
					.requestMatchers("/admin").hasRole("ADMIN") 		// accès réservé
					.anyRequest().authenticated() 						// tout le reste : login requis
					)
			.formLogin(form -> form
					.loginPage("/login") 					// notre page personnalisée
					.defaultSuccessUrl("/home", true) 		// redirigé après login
					.permitAll()					
					)
			.logout(logout -> logout
					.logoutSuccessUrl("/login?logout")
					.permitAll())
			.exceptionHandling(ex -> ex
					.accessDeniedPage("/error/403"))
			.csrf(csrf -> csrf.disable()); 			// désactive CSRF (optionnel, utile pour tests) // à activer en prod !
		
		return http.build();
	}

}
