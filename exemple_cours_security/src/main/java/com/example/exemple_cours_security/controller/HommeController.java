package com.example.exemple_cours_security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HommeController {

	@GetMapping("/login")
	public String login() {
		return "login"; 	
	}
	
	@GetMapping("/") 
	public String root() {
		return "redirect:/login";
	}
	
	@GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Bienvenue sur la page securisee !");
        return "home"; 
	}
	
	@GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("message", "Page ADMIN : accès reserve !");
        return "home";
    }
}
