package com.example.securite.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.securite.model.Medicament;
import com.example.securite.repository.MedicamentRepository;

@Controller
@RequestMapping("/ventes")
public class VenteController {

    private final MedicamentRepository medicamentRepository;

    public VenteController(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("medicaments", medicamentRepository.findAll());
        return "ventes";
    }

    @PostMapping
    public String vendre(@RequestParam Long medicamentId,
                         @RequestParam int quantite,
                         RedirectAttributes ra) {

        Medicament m = medicamentRepository.findById(medicamentId).orElse(null);

        if (m == null) {
            ra.addFlashAttribute("erreur", "Médicament introuvable.");
        } else if (quantite <= 0) {
            ra.addFlashAttribute("erreur", "La quantité doit être supérieure à 0.");
        } else if (quantite > m.getQuantiteStock()) {
            ra.addFlashAttribute("erreur",
                    "Stock insuffisant pour " + m.getNom() + " (disponible : " + m.getQuantiteStock() + ").");
        } else {
            m.setQuantiteStock(m.getQuantiteStock() - quantite);
            medicamentRepository.save(m);

            BigDecimal total = m.getPrix().multiply(BigDecimal.valueOf(quantite));
            ra.addFlashAttribute("succes",
                    "Vente enregistrée : " + quantite + " × " + m.getNom() + " = " + total + " DH");
        }
        return "redirect:/ventes";
    }
}