package com.example.exemple_cours_security.dao;


import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.exemple_cours_security.model.Role;
import com.example.exemple_cours_security.model.User;
import com.example.exemple_cours_security.repository.RoleRepository;
import com.example.exemple_cours_security.repository.UserRepository;

//@Configuration //à désactiver pour remplir la base de données
public class DataInit {

    private final RoleRepository roleRepository;

    DataInit(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

	@Bean
    CommandLineRunner init(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder encoder) {
		return args -> {
			Role roleUser = new Role();
			roleUser.setName("USER");
			Role roleAdmin = new Role();
			roleAdmin.setName("ADMIN");
			roleRepo.save(roleUser);
			roleRepo.save(roleAdmin);
			
			User user = new User();
			user.setUsername("user");
			user.setPassword(encoder.encode("user"));
			user.setRoles(Set.of(roleUser));
			
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRoles(Set.of(roleAdmin));

            userRepo.save(user);
            userRepo.save(admin);
		};
	}
}
