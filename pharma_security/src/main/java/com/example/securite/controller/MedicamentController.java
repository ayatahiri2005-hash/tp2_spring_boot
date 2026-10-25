package com.example.securite.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.securite.model.Medicament;
import com.example.securite.repository.MedicamentRepository;

@Controller
@RequestMapping("/medicaments")
public class MedicamentController {

    private final MedicamentRepository medicamentRepository;

    public MedicamentController(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    @GetMapping
    public String liste(Model model) {
        model.addAttribute("medicaments", medicamentRepository.findAll());
        return "medicaments";
    }

    // ADMIN uniquement (règle dans SecurityConfig)
    @PostMapping("/ajouter")
    public String ajouter(@RequestParam String nom,
                          @RequestParam(required = false) String description,
                          @RequestParam BigDecimal prix,
                          @RequestParam int quantiteStock,
                          RedirectAttributes ra) {

        if (nom.isBlank() || prix.signum() < 0 || quantiteStock < 0) {
            ra.addFlashAttribute("erreur", "Données invalides : nom obligatoire, prix et stock positifs.");
            return "redirect:/medicaments";
        }

        Medicament m = new Medicament();
        m.setNom(nom.trim());
        m.setDescription(description);
        m.setPrix(prix);
        m.setQuantiteStock(quantiteStock);
        medicamentRepository.save(m);

        ra.addFlashAttribute("succes", "Médicament ajouté : " + m.getNom());
        return "redirect:/medicaments";
    }

    // ADMIN et PHARMACIEN
    @PostMapping("/stock/{id}")
    public String modifierStock(@PathVariable Long id,
                                @RequestParam int quantiteStock,
                                RedirectAttributes ra) {

        Medicament m = medicamentRepository.findById(id).orElse(null);

        if (m == null) {
            ra.addFlashAttribute("erreur", "Médicament introuvable.");
        } else if (quantiteStock < 0) {
            ra.addFlashAttribute("erreur", "La quantité ne peut pas être négative.");
        } else {
            m.setQuantiteStock(quantiteStock);
            medicamentRepository.save(m);
            ra.addFlashAttribute("succes", "Stock mis à jour : " + m.getNom() + " (" + quantiteStock + ")");
        }
        return "redirect:/medicaments";
    }

    // ADMIN uniquement
    @PostMapping("/supprimer/{id}")
    public String supprimer(@PathVariable Long id, RedirectAttributes ra) {
        if (medicamentRepository.existsById(id)) {
            medicamentRepository.deleteById(id);
            ra.addFlashAttribute("succes", "Médicament supprimé.");
        } else {
            ra.addFlashAttribute("erreur", "Médicament introuvable.");
        }
        return "redirect:/medicaments";
    }
}