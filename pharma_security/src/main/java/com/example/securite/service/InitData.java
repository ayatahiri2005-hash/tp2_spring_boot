package com.example.securite.service;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.securite.model.Medicament;
import com.example.securite.model.Role;
import com.example.securite.model.User;
import com.example.securite.repository.MedicamentRepository;
import com.example.securite.repository.RoleRepository;
import com.example.securite.repository.UserRepository;

@Service
public class InitData implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final MedicamentRepository medicamentRepository;
    private final PasswordEncoder passwordEncoder;

    public InitData(RoleRepository roleRepository,
                    UserRepository userRepository,
                    MedicamentRepository medicamentRepository,
                    PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.medicamentRepository = medicamentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // 1. Les rôles
        for (String nom : new String[] { "ADMIN", "PHARMACIEN", "CAISSIER", "CLIENT" }) {
            if (roleRepository.findByNom(nom).isEmpty()) {
                roleRepository.save(new Role(nom));
            }
        }

        // 2. Les utilisateurs de test (mots de passe du sujet) + un client
        creerUtilisateur("admin", "admin123", "ADMIN");
        creerUtilisateur("pharma", "pharma123", "PHARMACIEN");
        creerUtilisateur("caisse", "caisse123", "CAISSIER");
        creerUtilisateur("client", "client123", "CLIENT");

        // 3. Quelques médicaments
        if (medicamentRepository.count() == 0) {
            creerMedicament("Doliprane 500mg", "Antalgique et antipyrétique.", "25.50", 100);
            creerMedicament("Amoxicilline 500mg", "Antibiotique à large spectre.", "45.00", 40);
            creerMedicament("Vitamine C 1000mg", "Renforce les défenses immunitaires.", "30.00", 75);
            creerMedicament("Bétadine Solution 10%", "Désinfectant pour usage externe.", "18.00", 20);
        }
    }

    private void creerUtilisateur(String username, String motDePasse, String roleNom) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        Role role = roleRepository.findByNom(roleNom).orElseThrow();

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(motDePasse)); // chiffrement BCrypt
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private void creerMedicament(String nom, String description, String prix, int stock) {
        Medicament m = new Medicament();
        m.setNom(nom);
        m.setDescription(description);
        m.setPrix(new BigDecimal(prix));
        m.setQuantiteStock(stock);
        medicamentRepository.save(m);
    }
}