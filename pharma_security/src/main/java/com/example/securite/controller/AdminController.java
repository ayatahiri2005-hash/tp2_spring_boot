package com.example.securite.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.securite.model.Role;
import com.example.securite.model.User;
import com.example.securite.repository.RoleRepository;
import com.example.securite.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("utilisateurs", userRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin";
    }

    @PostMapping("/utilisateurs/ajouter")
    public String ajouter(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam Long roleId,
                          RedirectAttributes ra) {

        Role role = roleRepository.findById(roleId).orElse(null);

        if (username.isBlank() || password.length() < 6) {
            ra.addFlashAttribute("erreur", "Identifiant obligatoire et mot de passe d'au moins 6 caractères.");
        } else if (userRepository.findByUsername(username.trim()).isPresent()) {
            ra.addFlashAttribute("erreur", "Cet identifiant existe déjà.");
        } else if (role == null) {
            ra.addFlashAttribute("erreur", "Rôle introuvable.");
        } else {
            User user = new User();
            user.setUsername(username.trim());
            user.setPassword(passwordEncoder.encode(password));   // toujours chiffré
            user.getRoles().add(role);
            userRepository.save(user);
            ra.addFlashAttribute("succes", "Utilisateur créé : " + user.getUsername());
        }
        return "redirect:/admin";
    }

    @PostMapping("/utilisateurs/{id}/activer")
    public String changerEtat(@PathVariable Long id, Authentication authentication, RedirectAttributes ra) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("erreur", "Utilisateur introuvable.");
        } else if (user.getUsername().equals(authentication.getName())) {
            ra.addFlashAttribute("erreur", "Vous ne pouvez pas désactiver votre propre compte.");
        } else {
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
            ra.addFlashAttribute("succes",
                    "Compte " + user.getUsername() + (user.isEnabled() ? " activé." : " désactivé."));
        }
        return "redirect:/admin";
    }

    @PostMapping("/utilisateurs/{id}/supprimer")
    public String supprimer(@PathVariable Long id, Authentication authentication, RedirectAttributes ra) {

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("erreur", "Utilisateur introuvable.");
        } else if (user.getUsername().equals(authentication.getName())) {
            ra.addFlashAttribute("erreur", "Vous ne pouvez pas supprimer votre propre compte.");
        } else {
            userRepository.delete(user);
            ra.addFlashAttribute("succes", "Utilisateur supprimé : " + user.getUsername());
        }
        return "redirect:/admin";
    }
}